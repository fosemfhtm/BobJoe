package food.map.mapview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import food.map.R
import food.map.utils.JsonController

class MapViewFragment: Fragment(), OnMapReadyCallback {
    private lateinit var v: View
    private lateinit var naverMap: NaverMap
    private lateinit var mapFragment: MapFragment
    private lateinit var jsonController: JsonController
    private lateinit var dongSet: ArrayList<String>
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var markerList: ArrayList<Marker>
    private var loaded = false

    companion object{
        fun newInstance(): MapViewFragment {
            val args = Bundle().apply {
                //putString("test", "test")
            }

            val fragment = MapViewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_map, container, false)

        markerList = arrayListOf()
        jsonController = JsonController(requireContext())

        val fm = childFragmentManager
        mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        dongSet = jsonController.makeDongSet()

        val spinner: Spinner = v.findViewById(R.id.spinner)
        arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dongSet
        )
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (loaded) {
                    markerList.forEach {
                        it.map = null
                    }
                    val pickerLocationList = jsonController.readFromJson2()
                    pickerLocationList.forEach {
                        if (it.dong == dongSet[p2])
                            naverMap.putMarkers(LatLng(it.y, it.x))
                    }
                }
                else
                    Toast.makeText(context, "정보를 불러오는 중입니다..", Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        mapFragment.getMapAsync(this)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        arrayAdapter.clear()
        jsonController.makeDongSet().forEach {
            arrayAdapter.add(it)
        }
        arrayAdapter.notifyDataSetChanged()
    }


    override fun onMapReady(nm: NaverMap) {
        naverMap = nm
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(36.3986924, 127.4024869)))

        if (!loaded){
            val pickerLocationList = jsonController.readFromJson2()
            pickerLocationList.forEach {
                if (it.dong == dongSet[0])
                    naverMap.putMarkers(LatLng(it.y, it.x))
            }
        }
        loaded = true
    }

    private fun NaverMap.putMarkers(latlng: LatLng) {
        val marker = Marker()
        marker.width = 50
        marker.height = 80
        marker.position = latlng
        marker.map = this

        markerList.add(marker)
    }
}