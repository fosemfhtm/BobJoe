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
    private var data = arrayListOf<PhonePage>()
    private lateinit var adapter: PhoneBookAdapter

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            adapter.apply {
                itemList = jsonController.readFromJson()
                notifyItemInserted(0)
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
        data = jsonController.readFromJson()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhonebookBinding.inflate(inflater, container, false)

        adapter = PhoneBookAdapter(data, LayoutInflater.from(context))
        binding.rvPhonebook.adapter = adapter
        binding.rvPhonebook.layoutManager = LinearLayoutManager(context)
        binding.fab.setOnClickListener {
            launcher.launch(Intent(context, AddPhoneActivity::class.java))
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val img: ImageView = itemView.findViewById(R.id.iv_profile)

        init {
            img.background = ShapeDrawable(OvalShape())
            img.clipToOutline = true
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
    }
}