package com.example.runnect.presentation.prestart

import android.content.ContentValues
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import androidx.activity.viewModels
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.databinding.ActivityPreStartBinding
import com.example.runnect.presentation.draw.DrawActivity
import com.example.runnect.presentation.search.entity.SearchResultEntity
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import timber.log.Timber

class PreStartActivity : BindingActivity<ActivityPreStartBinding>(R.layout.activity_pre_start),
    OnMapReadyCallback  {


    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var searchResult: SearchResultEntity


    val viewModel: PreStartViewModel by viewModels()


    private fun initView() {

        //MapFragment 추가
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this) //지도 객체 얻어오기
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this


        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = intent.getParcelableExtra(SEARCH_RESULT_EXTRA_KEY)
                    ?: throw Exception("데이터가 존재하지 않습니다.")

                Timber.tag(ContentValues.TAG).d("intent : ${searchResult}")
//
                viewModel.searchResult.value = searchResult
                Timber.tag(ContentValues.TAG).d("viewModel : ${viewModel.searchResult.value}")

                initView()
                goToDraw()
            }
        }


    }

    private fun goToDraw() {
        binding.btnPreStart.setOnClickListener {
            Timber.tag(ContentValues.TAG).d("searchResult : ${searchResult}")


            val intent = Intent(this, DrawActivity::class.java).apply {
                putExtra("searchResult", searchResult)
            }
            startActivity(intent)
        }
    }


    private fun makeMarker() {

        val startLatLng = searchResult.locationLatLng


        val marker = Marker()

        marker.position = LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble())
        marker.anchor = PointF(0.5f, 0.5f)
        marker.icon = OverlayImage.fromResource(R.drawable.startmarker)
        marker.map = naverMap


//        val infoWindow = InfoWindow()
//
//        infoWindow.adapter = object : infoAdapter(context, binding.root as ViewGroup) {
//        }
//
//
//
//        infoWindow.position = LatLng(
//            viewModel.searchResult.value!!.locationLatLng.latitude.toDouble(),
//            viewModel.searchResult.value!!.locationLatLng.longitude.toDouble()
//        )
//        infoWindow.anchor = PointF(0.5f, 1f)
//        infoWindow.open(marker, Align.Top)

        cameraUpdate(
            LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble())
        )


    }

    override fun onMapReady(map: NaverMap) {

        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0


        //네이버 맵 sdk에 위치 정보 제공
        locationSource =
            FusedLocationSource(this@PreStartActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        makeMarker()

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false


    }


    private fun cameraUpdate(location: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val SEARCH_RESULT_EXTRA_KEY = "SearchResult"
        const val SEARCH_RESULT_BEFORE_RUN = "SearchResultBeforeRun"

    }
}
