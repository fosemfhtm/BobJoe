package food.map.phone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.icu.text.IDNA
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.gun0912.tedpermission.provider.TedPermissionProvider
import food.map.R
import food.map.data.MapData
import food.map.data.PhonePage
import food.map.data.PickerLocation
import food.map.databinding.FragmentPhonebookBinding
import food.map.utils.JsonController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.gun0912.tedpermission.provider.TedPermissionProvider.context




class PhoneBookFragment: Fragment() {
    private var _binding: FragmentPhonebookBinding? = null
    val binding get() = _binding!!
    private var phoneData = arrayListOf<PhonePage>()
    private lateinit var adapter: PhoneBookAdapter
    private lateinit var pickerLocationList: ArrayList<PickerLocation>
    var loaded = false
    var refreshed = true

    private val addLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            refreshed = false
            adapter.apply {
                val list = jsonController.readFromJson()
                itemList = list
                notifyDataSetChanged()

                val locList = list.map { page -> page.location }

                CoroutineScope(Dispatchers.Main).launch {
                    val mapDataList = withContext(Dispatchers.IO){
                        locList.map { loc -> jsonController.addressToGeoCode(loc) }
                    }
                    pickerLocationList = convertMapDataListToLocationSet(mapDataList, list)
                    refreshed = true
                    Log.d("rrr", pickerLocationList[pickerLocationList.size-1].toString())
                    val jsonstring = Gson().toJson(pickerLocationList)
                    jsonController.constructJson(jsonstring)
                }
            }
        }
    }


    fun convertMapDataListToLocationSet(mapDataList: List<MapData?>, phonePageList: ArrayList<PhonePage>):ArrayList<PickerLocation>{
        val picker_location_list = arrayListOf<PickerLocation>()
        for (i in mapDataList.indices){
            var picker_location = PickerLocation("", 0.0, 0.0, "" ,-1 )
            if (mapDataList[i]!!.addresses == listOf<MapData.Address>()) {
                picker_location = PickerLocation("", 0.0, 0.0, "" ,-1 )
            }
            else{
                val dong = mapDataList[i]!!.addresses[0].addressElements[2].longName
                val x = mapDataList[i]!!.addresses[0].x.toDouble()
                val y = mapDataList[i]!!.addresses[0].y.toDouble()
                val name = phonePageList[i].name
                val type = phonePageList[i].type
                picker_location = PickerLocation(name, x, y, dong ,type )
            }
            picker_location_list.add(picker_location)
        }
        return picker_location_list
    }

    private val infoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            adapter.apply {
                itemList = jsonController.readFromJson()
                val jsonstring = Gson().toJson(pickerLocationList)
                jsonController.constructJson(jsonstring)
                notifyDataSetChanged()
            }
        }
    }
    private lateinit var jsonController: JsonController

    companion object{
        fun newInstance(): PhoneBookFragment {
            val args = Bundle().apply {
                //putString("test", "test")
            }

            val fragment = PhoneBookFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jsonController = JsonController(requireContext())
        phoneData = jsonController.readFromJson()

        val locList = phoneData.map { page -> page.location }

        updateDongSet(locList)

    }

    private fun updateDongSet(locList: List<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            val mapDataList = withContext(Dispatchers.IO) {
                locList.map { loc -> jsonController.addressToGeoCode(loc) }
            }

            pickerLocationList = convertMapDataListToLocationSet(mapDataList, phoneData)
            loaded = true
            val jsonstring = Gson().toJson(pickerLocationList)
            jsonController.constructJson(jsonstring)
        }
    }

    override fun onResume() {
        super.onResume()
        phoneData = jsonController.readFromJson()
        updateDongSet(phoneData.map { page -> page.location })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhonebookBinding.inflate(inflater, container, false)

        adapter = PhoneBookAdapter(phoneData, LayoutInflater.from(context))
        adapter.setOnItemClickListener(object : PhoneBookAdapter.OnItemClickListener{
            override fun onItemClick(v: View, pos: Int) {
                if (loaded && refreshed){
                    infoLauncher.launch(Intent(context, InfoActivity::class.java).apply {
                        putExtra("pos", pos)
                            .putExtra("name", phoneData[pos].name)
                            .putExtra("phone", phoneData[pos].phone)
                            .putExtra("loc", phoneData[pos].location)
                            .putExtra("dong", pickerLocationList[pos].dong)
                    })
                }
                else
                    Toast.makeText(context, "데이터를 불러오는 중입니다..", Toast.LENGTH_SHORT).show()
            }

        })
        binding.rvPhonebook.adapter = adapter
        binding.rvPhonebook.layoutManager = LinearLayoutManager(context)
        binding.rvPhonebook.addItemDecoration(DividerItemDecoration(context, 1))
        binding.fab.setOnClickListener {
            addLauncher.launch(Intent(context, AddPhoneActivity::class.java))
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

class PhoneBookAdapter(var itemList: ArrayList<PhonePage>, private val inflater: LayoutInflater) :
    RecyclerView.Adapter<PhoneBookAdapter.ViewHolder>(){

    interface OnItemClickListener{
        fun onItemClick(v: View, pos: Int)
    }
    private lateinit var mListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.mListener = listener
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.tv_title)
        val img: ImageView = itemView.findViewById(R.id.iv_profile)

        init {
            img.clipToOutline = true

            itemView.setOnClickListener {
                mListener.onItemClick(itemView, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.rv_item_phonebook, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name

        when(itemList[position].type){
            0 -> holder.img.setImageResource(R.drawable.ic_bob)
            1 -> holder.img.setImageResource(R.drawable.ic_dimsum)
            2 -> holder.img.setImageResource(R.drawable.ic_sushi)
            3 -> holder.img.setImageResource(R.drawable.ic_spagetti)
            4 -> holder.img.setImageResource(R.drawable.ic_hamberger)
            5 -> holder.img.setImageResource(R.drawable.ic_yasik)
        }
    }
}