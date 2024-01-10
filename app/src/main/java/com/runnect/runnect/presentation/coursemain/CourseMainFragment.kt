package com.runnect.runnect.presentation.coursemain

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentCourseMainBinding
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.util.extension.PermissionUtil
import com.runnect.runnect.util.extension.showToast


class CourseMainFragment :
    BindingFragment<FragmentCourseMainBinding>(R.layout.fragment_course_main),
    OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var currentLocation: LatLng = LatLng(37.52901832956373, 126.9136196847032) //국회의사당 좌표
    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        checkAndRequestLocationPermission()
    }

    private fun init() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        initView()
        initCurrentLocationButtonClickListener()
        initDrawCourseButtonClickListener()
    }

    private fun initView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }


    private fun checkAndRequestLocationPermission() {
        if (isLocationPermissionGranted()) {
            if (!::naverMap.isInitialized) {
                initView()
            }
        } else {
            context?.let {
                PermissionUtil.requestLocationPermission(
                    context = it,
                    onPermissionGranted = { updateCamera(currentLocation) },
                    onPermissionDenied = { showPermissionDeniedToast() },
                    permissionType = PermissionUtil.PermissionType.LOCATION
                )
            }
        }
    }


    private fun initCurrentLocationButtonClickListener() {
        binding.btnCurrentLocation.setOnClickListener {
            if (isLocationPermissionGranted()) {
                updateCamera(currentLocation)
            } else {
                context?.let {
                    PermissionUtil.requestLocationPermission(
                        context = it,
                        onPermissionGranted = { updateCamera(currentLocation) },
                        onPermissionDenied = { showPermissionDeniedToast() },
                        permissionType = PermissionUtil.PermissionType.LOCATION
                    )
                }
            }
        }
    }

    private fun initDrawCourseButtonClickListener() {
        binding.btnDraw.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        map.locationSource = locationSource

        if (isLocationPermissionGranted()) {
            map.locationTrackingMode = LocationTrackingMode.Follow //위치추적 모드 Follow
        }

        //네이버 맵 sdk에 위치 정보 제공
        locationSource = FusedLocationSource(
            this,
            LOCATION_PERMISSION_REQUEST_CODE
        )
        naverMap.locationSource = locationSource

        naverMap.addOnLocationChangeListener { location ->
            currentLocation = LatLng(location.latitude, location.longitude)
            map.locationOverlay.run { //현재 위치 마커
                isVisible = true //현재 위치 마커 가시성(default = false)
                position = LatLng(currentLocation.latitude, currentLocation.longitude)
            }
        }

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false

        //현위치 커스텀 이미지
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.current_location)
    }

    private fun isLocationPermissionGranted(): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun updateCamera(location: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

    }

    private fun showPermissionDeniedToast() {
        showToast(getString(R.string.location_permission_denied))
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}