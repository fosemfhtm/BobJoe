package food.map.mapview

import android.os.Bundle
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
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.MarkerIcons
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
    private var selected = 0

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
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_list)

        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (loaded) {
                    markerList.forEach {
                        it.map = null
                    }
                    var latAvg = 0.0
                    var lngAvg = 0.0
                    var count = 0

                    val pickerLocationList = jsonController.readFromJson2()
                    pickerLocationList.forEach {
                        if (it.dong == dongSet[p2]){
                            naverMap.putMarkers(LatLng(it.y, it.x), it.type, it.name)
                            latAvg += it.y
                            lngAvg += it.x
                            count++
                        }
                    }
                    latAvg /= count
                    lngAvg /= count
                    naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latAvg, lngAvg)))
                }
                else
                    Toast.makeText(context, "정보를 불러오는 중입니다..", Toast.LENGTH_SHORT).show()

                selected = p2
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
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(nm: NaverMap) {
        naverMap = nm

        val list = jsonController.readFromJson2()
        var count = 0
        var latAvg = 0.0
        var lngAvg = 0.0
        list.forEach {
            if (it.dong == dongSet[selected]){
                naverMap.putMarkers(LatLng(it.y, it.x), it.type, it.name)
                latAvg += it.y
                lngAvg += it.x
                count++
            }
        }

        latAvg /= count
        lngAvg /= count

        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latAvg, lngAvg)))

        loaded = true
    }

    private fun NaverMap.putMarkers(latlng: LatLng, type: Int, name: String) {
        val marker = Marker()

        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "정보 창 내용"
            }
        }

        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                // 정보 창이 열린 마커의 tag를 텍스트로 노출하도록 반환
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        val listener = Overlay.OnClickListener { overlay ->
            val _marker = overlay as Marker

            if (_marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(_marker)
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }

            true
        }

        marker.width = 50
        marker.height = 80
        marker.icon = MarkerIcons.BLACK
        when(type){
            0 -> {marker.iconTintColor = Integer.parseInt("F7C100", 16)}
            1 -> {marker.iconTintColor = Integer.parseInt("EC3843", 16)}
            2 -> {marker.iconTintColor = Integer.parseInt("B2D135", 16)}
            3 -> {marker.iconTintColor = Integer.parseInt("8C6D41", 16)}
            4 -> {marker.iconTintColor = Integer.parseInt("D6689D", 16)}
            5 -> {marker.iconTintColor = Integer.parseInt("00BAC9", 16)}
        }
        marker.tag = name
        marker.onClickListener = listener

        marker.position = latlng
        marker.map = this

        markerList.add(marker)
    }
}