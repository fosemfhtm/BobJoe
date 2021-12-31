package food.map.phone

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import food.map.R
import food.map.data.PhonePage
import food.map.databinding.FragmentPhonebookBinding

class PhoneBookFragment: Fragment() {
    private var _binding: FragmentPhonebookBinding? = null
    private val binding get() = _binding!!
    private var phoneData = arrayListOf<PhonePage>()
    private lateinit var adapter: PhoneBookAdapter

    private val addLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            adapter.apply {
                itemList = jsonController.readFromJson()
                notifyDataSetChanged()
                //notifyItemInserted(phoneData.size-1)
            }
        }
    }

    private val infoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            adapter.apply {
                itemList = jsonController.readFromJson()
                notifyDataSetChanged()
                //notifyItemRemoved(clickedPos)
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
        val name: TextView = itemView.findViewById(R.id.tv_name)
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