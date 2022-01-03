package food.map.phone

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
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
import food.map.R
import food.map.data.MapData
import food.map.data.PhonePage
import food.map.data.PickerLocation
import food.map.databinding.FragmentPhonebookBinding
import food.map.MapFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class PhoneBookFragment: Fragment() {
    private var _binding: FragmentPhonebookBinding? = null
    private val binding get() = _binding!!
    private var phoneData = arrayListOf<PhonePage>()
    private lateinit var adapter: PhoneBookAdapter

    private val addLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            adapter.apply {
                val list = jsonController.readFromJson()
                itemList = list
                notifyDataSetChanged()

                val locList = list.map { page -> page.location }
                Log.d("locList", locList.toString())

                CoroutineScope(Dispatchers.Main).launch {
                    val mapDataList = withContext(Dispatchers.IO){
                        locList.map { loc -> jsonController.addressToGeoCode(loc) }
                    }
                    Log.d("geoLoc", mapDataList[mapDataList.size-1].toString())

                    val picker_location_list = convertMapDataListToLocationSet(mapDataList, list)

                    val jsonstring = Gson().toJson(picker_location_list)
                    constructJson(jsonstring)
                }
            }
            val dongset = jsonController.makeDongSet()
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
            Log.d("picker_location", picker_location.dong)
            picker_location_list.add(picker_location)
        }
        return picker_location_list
    }

    private fun constructJson(jsonString: String) {
        val filePath = requireContext().getExternalFilesDir(null)!!.path + "/pickerlocationlist.json"

        val file = File(filePath)
        file.delete()

        val newFile = File(filePath)
        val fileWriter = FileWriter(newFile, false)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.append(jsonString)
        bufferedWriter.close()
    }

    private val infoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            adapter.apply {
                itemList = jsonController.readFromJson()
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
        Log.d("locList", locList.toString())

        CoroutineScope(Dispatchers.Main).launch {
            val mapDataList = withContext(Dispatchers.IO) {
                locList.map { loc -> jsonController.addressToGeoCode(loc) }
            }
            Log.d("geoLoc", mapDataList[mapDataList.size - 1].toString())

            val picker_location_list = convertMapDataListToLocationSet(mapDataList, phoneData)

            val jsonstring = Gson().toJson(picker_location_list)
            constructJson(jsonstring)
        }

    }

    override fun onResume() {
        super.onResume()
        phoneData = jsonController.readFromJson()
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
                infoLauncher.launch(Intent(context, InfoActivity::class.java).apply {
                    putExtra("pos", pos)
                        .putExtra("name", phoneData[pos].name)
                        .putExtra("phone", phoneData[pos].phone)
                        .putExtra("loc", phoneData[pos].location)
                })
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
            img.background = ShapeDrawable(OvalShape())
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
            0 -> holder.img.setImageResource(R.drawable.ic_chicken)
            1 -> holder.img.setImageResource(R.drawable.ic_korean)
            2 -> holder.img.setImageResource(R.drawable.ic_japanese)
            3 -> holder.img.setImageResource(R.drawable.ic_noodle)
            4 -> holder.img.setImageResource(R.drawable.ic_pig)
            5 -> holder.img.setImageResource(R.drawable.ic_pizza)
        }
    }
}