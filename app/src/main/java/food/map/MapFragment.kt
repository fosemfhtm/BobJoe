package food.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import food.map.api.ApiClient
import food.map.api.ApiInterface
import food.map.data.MapData
import food.map.data.PhonePage
import food.map.data.PickerLocation
import food.map.databinding.FragmentMapBinding
import food.map.phone.JsonController
import food.map.phone.MAP_KEY_ID
import food.map.phone.MAP_KEY_SECRET
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class MapFragment: Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var jsonController :JsonController

    companion object{
        fun newInstance(): MapFragment {
            val args = Bundle().apply {
                //putString("test", "test")
            }

            val fragment = MapFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        var dongset = jsonController.makeDongSet()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}