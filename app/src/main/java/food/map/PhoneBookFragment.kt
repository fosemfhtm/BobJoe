package food.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import food.map.databinding.FragmentPhonebookBinding
import java.io.*

class PhoneBookFragment: Fragment() {
    private var _binding: FragmentPhonebookBinding? = null
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhonebookBinding.inflate(inflater, container, false)


        val filePath = requireContext().getExternalFilesDir(null)!!.path + "/phonebook.json"

        val file = File(filePath)
        if(!file.exists()) makeNewJson(file) //Create new if there is no one
        val jsonString = getSavedJson(file) //Read saved json file

        //JsonArray -> List (Gson)
        val list: List<PhonePage> = Gson().fromJson(jsonString , Array<PhonePage>::class.java).toList()
        val arrayList = arrayListOf<PhonePage>()
        arrayList.addAll(list)
        arrayList.add(PhonePage("BBQ", "042-112-3456", "대구 어딘가"))

        // List -> Json (Gson)
        val json = Gson().toJson(arrayList)
        Log.d("data1", json)

        return binding.root
    }

    private fun makeNewJson(file: File) {
        val assetManager = resources.assets
        val inputStream= assetManager.open("phonebook.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val fileWriter = FileWriter(file, false)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.append(jsonString)
        bufferedWriter.close()
    }

    private fun getSavedJson(file: File): String{
        var jsonString = ""
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        for (readLine in bufferedReader.readLines()) {
            jsonString += readLine
        }
        return jsonString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textview1.text = "1234"
    }
}