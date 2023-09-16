package com.runnect.runnect.presentation.draw

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.data.dto.UploadLatLng
import com.runnect.runnect.databinding.ActivityDrawBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.ContentUriRequestBody
import com.runnect.runnect.util.extension.setActivityDialog
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_make_course.view.btn_run
import kotlinx.android.synthetic.main.custom_dialog_make_course.view.btn_storage
import kotlinx.android.synthetic.main.custom_dialog_require_login.view.btn_cancel
import kotlinx.android.synthetic.main.custom_dialog_require_login.view.btn_login
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class DrawActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityDrawBinding>(R.layout.activity_draw),
    OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var currentLocation: LatLng
    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수)

    var isCustomLocationMode: Boolean = false
    var isSearchLocationMode: Boolean = false
    var isCurrentLocationMode: Boolean = false

    private lateinit var naverMap: NaverMap
    private lateinit var departureLatLng: LatLng
    private lateinit var animDown: Animation
    private lateinit var animUp: Animation
    private lateinit var searchResult: SearchResultEntity
    private lateinit var captureUri: Uri

    private val coords = mutableListOf<LatLng>()
    private val path = PathOverlay()
    private val calcDistanceList = arrayListOf<LatLng>()
    private val touchList = arrayListOf<LatLng>()
    private val markerList = mutableListOf<Marker>()
    private val viewModel: DrawViewModel by viewModels()
    private var distanceSum: Float = 0.0f
    private var sumList = mutableListOf<Double>()
    private var isMarkerAvailable: Boolean = false
    var isVisitorMode: Boolean = MainActivity.isVisitorMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        searchResult =
            intent.getParcelableExtra(EXTRA_SEARCH_RESULT) ?: throw Exception("데이터가 존재하지 않습니다.")
        Timber.tag(ContentValues.TAG).d("searchResult : $searchResult")

        when (searchResult.mode) {
            "searchLocation" -> initSearchLocationMode()
            "currentLocation" -> initCurrentLocationMode()
            "customLocation" -> initCustomLocationMode()
        }
    }

    fun initCustomLocationMode() {
        isCustomLocationMode = true

        binding.customDepartureMarker.isVisible = true
        initView()
        addObserver()
        backButton()
        courseFinish()

        binding.btnPreStart.setOnClickListener {
            isMarkerAvailable = true

            showDrawGuide()
            hideDeparture()
            showDrawCourse()
        }
    }

    fun getLocationUsingLatLng(lat: Double, lon: Double) {
        viewModel.getLocationUsingLatLng(lat = lat, lon = lon)
    }

    fun initCurrentLocationMode() {
        isCurrentLocationMode = true

        binding.customDepartureMarker.isVisible = false
        initView()

        addObserver()
        backButton()
        courseFinish()

        isMarkerAvailable = true
        showDrawGuide()
        hideDeparture()
        showDrawCourse()
    }

    fun initSearchLocationMode() {
        isSearchLocationMode = true

        binding.customDepartureMarker.isVisible = false

        viewModel.searchResult.value = searchResult
        setDepartureLatLng(
            latLng = LatLng(
                searchResult.locationLatLng!!.latitude,
                searchResult.locationLatLng!!.longitude
            )
        )
        initView()
        addObserver() //마커 백버튼 활성화, 코스 생성 후 프로그레스바
        activateDrawCourse()
        backButton()
        courseFinish()
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun initView() {
        val fm = supportFragmentManager
        val mapFragment =
            fm.findFragmentById(R.id.mapView) as MapFragment? ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)

        //네이버 맵 sdk에 위치 정보 제공
        fusedLocation = LocationServices.getFusedLocationProviderClient(this) // 현위치
        locationSource = FusedLocationSource(
            this, LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        if (isCurrentLocationMode || isCustomLocationMode) {
            naverMap.locationSource = locationSource
            naverMap.locationTrackingMode =
                LocationTrackingMode.Follow //위치추적 모드 Follow - 자동으로 camera 이동
        }

        naverMap.addOnLocationChangeListener { location -> //사용자의 현위치를 받아오는 리스너
            currentLocation = LatLng(location.latitude, location.longitude)
            map.locationOverlay.run { //현재 위치 마커
                if (isCurrentLocationMode || isCustomLocationMode) {
                    isVisible = false //현재 위치 마커 가시성(default = false)
                } else true
                position = LatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                )
            }
            setDepartureLatLng(latLng = LatLng(currentLocation.latitude, currentLocation.longitude))
        }

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false

        //현위치 커스텀 이미지
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.current_location)

        lifecycleScope.launchWhenCreated {
            //현위치가 바뀌든 말든 저 콜백 스코프 바깥인 여기에 작성해주고 invisible하게 해주면 현위치 마커는 최초 1회만 생성됨
            //지금 현위치로 지도 이동 전에 세팅이 돼서 인위적으로 늦춰줬음 추후 코루틴으로 순서 보장해주면 됨.
            //tmap reverse API 이용해서 현위치 좌표로 장소/주소 값 세팅 후 서버 업로드 해줘야 함.
            delay(2000)
            drawCourse()
            if (!::currentLocation.isInitialized) {
                showToast("초기화 안 됨")
            }
        }

        if (isCustomLocationMode) {
            naverMap.addOnCameraChangeListener { _, _ ->
//                Timber.tag("카메라-변경중").d("변경중")
            }

            naverMap.addOnCameraIdleListener { //여기서 좌표로 장소/주소 받아오는 서버 통신 logic 돌려야 함.
                // 현재 지도의 중심 좌표를 가져옵니다.
                val cameraPosition = naverMap.cameraPosition
                val centerLatLng = cameraPosition.target // 중심 좌표

                if (::currentLocation.isInitialized) { //최초 1회 돌릴 때는 currentLocation이랑 centerLat
                    getLocationUsingLatLng(
                        lat = centerLatLng.latitude,
                        lon = centerLatLng.longitude
                    )
                    Timber.tag("초기화").d("currentLocation 초기화 완료")
                } else {
                    Timber.tag("초기화").d("currentLocation 초기화 아직 안 됨")
                }
                Timber.tag("카메라-끝").d("$centerLatLng")
            }
        }
    }

    private fun courseFinish() {
        binding.btnDraw.setOnClickListener {

            if (isVisitorMode) {
                requireVisitorLogin()
            } else {
                createMbr()
            }
        }
    }

    private fun activateDrawCourse() {
        binding.btnPreStart.setOnClickListener {
            isMarkerAvailable = true
            showDrawGuide()
            hideDeparture()
            showDrawCourse()
        }
    }

    private fun showDrawGuide() {
        with(binding) {
            frameDrawGuide.isVisible = true
            ivGuideLogo.isVisible = true
            tvDrawGuide.isVisible = true
        }
    }

    private fun showDrawCourse() {
        with(binding) {
            btnDraw.isVisible = true
            btnMarkerBack.isVisible = true
            frameCourseDistance.isVisible = true
            tvCourseDistanceRecord.isVisible = true
            tvCourseDistanceKm.isVisible = true
        }
    }

    private fun hideDeparture() {
        animDown = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
        animUp = AnimationUtils.loadAnimation(this, R.anim.slide_out_up)

        with(binding) {
            //Top invisible
            viewTopFrame.startAnimation(animUp)
            tvDeparture.startAnimation(animUp)
            viewTopFrame.isVisible = false
            tvDeparture.isVisible = false

            //Bottom invisible
            viewBottomSheetFrame.startAnimation(animDown)
            tvPlaceName.startAnimation(animDown)
            tvPlaceAddress.startAnimation(animDown)
            btnPreStart.startAnimation(animDown)

            viewBottomSheetFrame.isVisible = false
            tvPlaceName.isVisible = false
            tvPlaceAddress.isVisible = false
            btnPreStart.isVisible = false
        }
    }

    private fun addObserver() {
        observeIsBtnAvailable()
        observeDrawState()

        viewModel.reverseGeocodingResult.observe(this) {
            val buildingName = viewModel.reverseGeocodingResult.value!!.buildingName
            if (buildingName.isEmpty()) {
                binding.tvPlaceName.text = CUSTOM_DEPARTURE
            } else {
                binding.tvPlaceName.text = buildingName
            }
            binding.tvPlaceAddress.text =
                viewModel.reverseGeocodingResult.value?.fullAddress ?: "fail"
        }
    }

    private fun activateMarkerBackBtn() {
        with(binding) {
            btnMarkerBack.isEnabled = true
            btnMarkerBack.setImageResource(R.drawable.backcourse_enable_true)
            btnDraw.isEnabled = true
            btnDraw.setBackgroundResource(R.drawable.radius_10_m1_button)
        }
    }

    private fun deactivateMarkerBackBtn() {
        with(binding) {
            btnMarkerBack.isEnabled = false
            btnMarkerBack.setImageResource(R.drawable.backcourse_enable_false)
            btnDraw.isEnabled = false
            btnDraw.setBackgroundResource(R.drawable.radius_10_g3_button)
        }
    }

    private fun observeIsBtnAvailable() {
        viewModel.isBtnAvailable.observe(this) {
            if (viewModel.isBtnAvailable.value == true) {
                activateMarkerBackBtn()

            } else {
                deactivateMarkerBackBtn()
            }
        }
    }

    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    private fun observeDrawState() { //분기 처리를 더 해줘야 함. 서버에서 400 날아오는데 이게 success로 빠져서 '코스 생성 완료' 팝업이 뜨고 있음.
        viewModel.drawState.observe(this) {
            when (it) {
                UiState.Empty -> hideLoadingBar()
                UiState.Loading -> showLoadingBar()
                UiState.Success -> {
                    hideLoadingBar()
                    notifyCreateFinish()
                }

                UiState.Failure -> {
                    hideLoadingBar()
                }
            }
        }
    }

    private fun notifyCreateFinish() {
        val (dialog, dialogLayout) = setActivityDialog(
            layoutInflater = layoutInflater,
            view = binding.root,
            resId = R.layout.custom_dialog_make_course,
            cancel = false
        )

        with(dialogLayout) {
            this.btn_run.setOnClickListener {
                val intent = Intent(this@DrawActivity, CountDownActivity::class.java).apply {
                    putExtra(
                        EXTRA_COURSE_DATA, CourseData(
                            courseId = viewModel.uploadResult.value!!.data.course.id,
                            publicCourseId = null,
                            touchList = touchList,
                            startLatLng = departureLatLng,
                            departure = searchResult.name,
                            distance = viewModel.distanceSum.value!!,
                            image = captureUri.toString(),
                            dataFrom = "fromDrawCourse"
                        )
                    )
                }
                startActivity(intent)
                dialog.dismiss()
            }

            this.btn_storage.setOnClickListener {
                val intent = Intent(this@DrawActivity, MainActivity::class.java).apply {
                    putExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION, "fromDrawCourse")
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun requireVisitorLogin() {
        val (dialog, dialogLayout) = setActivityDialog(
            layoutInflater = layoutInflater,
            view = binding.root,
            resId = R.layout.custom_dialog_require_login,
            cancel = false
        )

        with(dialogLayout) {
            this.btn_login.setOnClickListener {
                val intent = Intent(this@DrawActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                dialog.dismiss()
            }
            this.btn_cancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun backButton() {
        binding.imgBtnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }


    //카메라 위치 변경 함수
    private fun cameraUpdate(location: Any) {
        //이건 맨 처음 지도 켤 때 startLocation으로 위치 옮길 때 사용
        if (location is LatLng) {
            val cameraUpdate = CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        }
        //이건 카메라 이동해서 캡쳐할 때 사용
        else if (location is LatLngBounds) {
            val cameraUpdate = CameraUpdate.fitBounds(location)
            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun drawCourse() {
        createDepartureMarker()
        createRouteMarker()
        deleteRouteMarker()
    }

    fun getCurrentLatLng() {
        naverMap.addOnLocationChangeListener { location ->
            currentLocation = LatLng(location.latitude, location.longitude)
        }
    }

    private fun createDepartureMarker() {
        if (isSearchLocationMode || isCurrentLocationMode) {
            setDepartureMarker()
        }
        addDepartureToCoords()
        addDepartureToCalcDistanceList()
    }

    private fun setDepartureLatLng(latLng: LatLng) {
        departureLatLng = LatLng(
            latLng.latitude, latLng.longitude
        )
    }

    private fun setDepartureMarker() { //
        val departureMarker = Marker()
        departureMarker.position = LatLng(departureLatLng.latitude, departureLatLng.longitude)
        departureMarker.anchor = PointF(0.5f, 0.7f)
        departureMarker.icon = OverlayImage.fromResource(R.drawable.marker_departure)
        departureMarker.map = naverMap

        if (isSearchLocationMode) {
            cameraUpdate(
                LatLng(departureLatLng.latitude, departureLatLng.longitude)
            ) // 현위치에서 출발할 때 이것 때문에 트랙킹 모드 활성화 시 카메라 이동하는 게 묻혔음
        }
    }

    private fun addDepartureToCoords() {
        coords.add(LatLng(departureLatLng.latitude, departureLatLng.longitude))
    }

    private fun addDepartureToCalcDistanceList() {
        calcDistanceList.add(
            LatLng(
                departureLatLng.latitude, departureLatLng.longitude
            )
        )
    }

    private fun createRouteMarker() {
        naverMap.setOnMapClickListener { _, coord ->
            if (isMarkerAvailable) {
                viewModel.isBtnAvailable.value = true
                if (touchList.size < MAX_MARKER_NUM) {
                    addCoordsToTouchList(coord)
                    setRouteMarker(coord)
                    generateRouteLine(coord)
                    calcDistance()
                } else {
                    Toast.makeText(this, "마커는 20개까지 생성 가능합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addCoordsToTouchList(coord: LatLng) {
        touchList.add(
            LatLng(
                coord.latitude, coord.longitude
            )
        )
    }

    private fun setRouteMarker(coord: LatLng) {
        val routeMarker = Marker()
        routeMarker.position = LatLng(
            coord.latitude, coord.longitude
        )
        routeMarker.anchor = PointF(0.5f, 0.5f)
        routeMarker.icon = OverlayImage.fromResource(R.drawable.marker_route)
        routeMarker.map = naverMap

        markerList.add(routeMarker)
    }

    private fun generateRouteLine(coord: LatLng) {
        coords.add(LatLng(coord.latitude, coord.longitude)) // coords에 터치로 받아온 좌표값 추가
        path.coords = coords // 경로선 그리기
        path.color = Color.parseColor("#593EEC") // 경로선 색상
        path.outlineColor = Color.parseColor("#593EEC") // 경로선 테두리 색상
        path.map = naverMap
    }

    private fun deleteRouteMarker() {
        binding.btnMarkerBack.setOnClickListener {
            updateRouteMarkerData()
            updateBtnAvailable()
            updateRouteLineData()
            reCalculateDistance()
            setDistanceToViewModel()
        }
    }

    private fun updateBtnAvailable() {
        if (touchList.isEmpty()) {
            viewModel.isBtnAvailable.value = false
        }
    }

    private fun updateRouteMarkerData() {
        touchList.removeLast()
        markerList.last().map = null
        markerList.removeLast()
    }

    private fun updateRouteLineData() {
        coords.removeLast()
        if (coords.size >= LEAST_CONDITION_CREATE_PATH) {
            path.coords = coords
            path.map = naverMap
        } else {
            path.map = null
        }
    }

    private fun reCalculateDistance() {
        if (calcDistanceList.isNotEmpty() && sumList.isNotEmpty()) {
            calcDistanceList.removeLast()
            sumList.removeLast()
        }
    }

    private fun setDistanceToViewModel() {
        distanceSum = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR).toFloat()
        viewModel.distanceSum.value = distanceSum
    }

    private fun calcDistance() {

        for (i in 0 until touchList.size) {
            if (!calcDistanceList.contains(touchList[i])) {
                calcDistanceList.add(touchList[i])
            }
        }
        for (num in 0..calcDistanceList.size - 2) {
            val distanceResult = viewModel.distance(
                calcDistanceList[num].latitude,
                calcDistanceList[num].longitude,
                calcDistanceList[num + 1].latitude,
                calcDistanceList[num + 1].longitude,
                DISTANCE_UNIT
            )
            if (!sumList.contains(distanceResult)) {
                sumList.add(distanceResult)
                distanceSum = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR).toFloat()
                viewModel.distanceSum.value = distanceSum
            }
        }
    }

    private fun createMbr() {
        val bounds = LatLngBounds.Builder()
            .include(LatLng(departureLatLng.latitude, departureLatLng.longitude)).include(touchList)
            .build()
        naverMap.setContentPadding(100, 100, 100, 100)
        cameraUpdate(bounds)
        captureMap()
    }


    private fun captureMap() {
        naverMap.takeSnapshot {
            val captureUri = getImageUri(it) //Bitmap -> Uri

            viewModel.setRequestBody(
                ContentUriRequestBody(
                    this, captureUri
                )
            ) //Uri -> RequestBody
            setViewModelValue(calcDistanceList)
            viewModel.uploadCourse()
        }
    }

    private fun setViewModelValue(distanceList: List<LatLng>) {
        val uploadLatLngList: List<UploadLatLng> = distanceList.map { latLng ->
            UploadLatLng(latLng.latitude, latLng.longitude)
        }
        viewModel.path.value = uploadLatLngList
        viewModel.departureAddress.value = searchResult.fullAddress
        viewModel.departureName.value = searchResult.name
    }

    // Get uri of images from camera function
    private fun getImageUri(inImage: Bitmap): Uri {

        val tempFile = File.createTempFile("temprentpk", ".png")
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        val uri = FileProvider.getUriForFile(
            this, BuildConfig.APPLICATION_ID + ".fileprovider", tempFile
        )
        captureUri = uri
        return uri
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val MAX_MARKER_NUM = 20
        const val DISTANCE_UNIT = "kilometer"
        const val LEAST_CONDITION_CREATE_PATH = 2
        const val EXTRA_SEARCH_RESULT = "searchResult"
        const val EXTRA_COURSE_DATA = "CourseData"
        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        const val CUSTOM_DEPARTURE = "내가 설정한 출발지"
    }
}