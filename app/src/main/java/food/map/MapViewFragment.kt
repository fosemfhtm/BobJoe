package food.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import food.map.phone.JsonController

class MapViewFragment: Fragment(), OnMapReadyCallback {
    private lateinit var v: View
    private lateinit var naverMap: NaverMap
    private lateinit var mapFragment: MapFragment

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

        val fm = childFragmentManager
        mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }


        mapFragment.getMapAsync(this)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        mapFragment.getMapAsync(this)
    }

    private fun putMarkers(p0: NaverMap) {

    }

    override fun onMapReady(nm: NaverMap) {
        naverMap = nm
        putMarkers(naverMap)

        val jsonController = JsonController(requireContext())
        val pickerLocationList = jsonController.readFromJson2()
        pickerLocationList.forEach {
            naverMap.putMarkers(LatLng(it.y, it.x))
        }

        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(36.3986924, 127.4024869)))
    }

    private fun NaverMap.putMarkers(latlng: LatLng) {
        val marker = Marker()
        marker.width = 50
        marker.height = 80
        marker.position = latlng
        marker.map = this
    }
}