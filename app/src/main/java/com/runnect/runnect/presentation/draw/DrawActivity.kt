package com.runnect.runnect.presentation.draw

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.data.dto.UploadLatLng
import com.runnect.runnect.databinding.ActivityDrawBinding
import com.runnect.runnect.databinding.BottomsheetRequireCourseNameBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.DepartureSetMode
import com.runnect.runnect.util.custom.dialog.RequireLoginDialogFragment
import com.runnect.runnect.util.extension.PermissionUtil
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.setActivityDialog
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.multipart.ContentUriRequestBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_make_course.view.btn_run
import kotlinx.android.synthetic.main.custom_dialog_make_course.view.btn_storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class DrawActivity : BindingActivity<ActivityDrawBinding>(R.layout.activity_draw),
    OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var currentLocation: LatLng
    private lateinit var fusedLocation: FusedLocationProviderClient // 현재 위치 반환 객체 변수

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

    var isFirstInit: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        initMapView()
        getSearchIntent()
        addObserver()
        backButton()
        courseFinish()
    }

    private fun getSearchIntent() {
        searchResult =
            intent.getParcelableExtra(EXTRA_SEARCH_RESULT) ?: throw Exception("데이터가 존재하지 않습니다.")
    }

    private fun initMapView() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this) // 현위치 정보 제공

        val fm = supportFragmentManager
        val mapFragment =
            fm.findFragmentById(R.id.mapView) as MapFragment? ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(
            this, LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        initMode()
        setLocationTrackingMode()
        setCurrentLocationIcon()
        setZoomControl()
        setLocationChangedListener()
        setCameraFinishedListener()
    }

    private fun initMode() {
        when (searchResult.mode) {
            DepartureSetMode.SEARCH.mode -> initSearchLocationMode()
            DepartureSetMode.CURRENT.mode -> initCurrentLocationMode()
            DepartureSetMode.CUSTOM.mode -> initCustomLocationMode()
            else -> throw IllegalArgumentException("Unknown mode: ${searchResult.mode}")
        }
    }

    private fun initSearchLocationMode() {
        isSearchLocationMode = true

        with(binding) {
            tvGuide.isVisible = false
        }

        viewModel.searchResult.value = searchResult
        viewModel.departureName.value =
            searchResult.name //searchLocationMode일 땐 departureName 값 세팅해주는 부분이 따로 없어서 여기에 작성해놓음

        setDepartureLatLng(
            latLng = LatLng(
                searchResult.locationLatLng!!.latitude, searchResult.locationLatLng!!.longitude
            )
        )
        activateDrawCourse()

        lifecycleScope.launch {
            delay(500) //인위적으로 늦춰줌
            if (::departureLatLng.isInitialized) {
                drawCourse(departureLatLng = departureLatLng)
            }
        }
    }

    private fun initCurrentLocationMode() {
        isCurrentLocationMode = true
        isMarkerAvailable = true

        with(binding) {
            customDepartureMarker.isVisible = false
            tvGuide.isVisible = false
        }
        showDrawGuide()
        hideDeparture()
        showDrawCourse()
    }

    private fun initCustomLocationMode() {
        isCustomLocationMode = true
        with(binding) {
            customDepartureMarker.isVisible = true
            customDepartureInfoWindow.isVisible = true
            tvCustomDepartureGuideFrame.isVisible = true

            btnPreStart.setOnClickListener {
                isMarkerAvailable = true
                showDrawGuide()
                hideDeparture()
                showDrawCourse()
                getCenterPosition().apply {
                    departureLatLng = this
                }.let(::drawCourse)
                hideFloatedDeparture()
            }
        }
    }

    private fun hideFloatedDeparture() {
        with(binding) {
            customDepartureMarker.isVisible = false
            customDepartureInfoWindow.isVisible = false
        }
    }

    private fun setDepartureLatLng(latLng: LatLng) {
        departureLatLng = LatLng(
            latLng.latitude, latLng.longitude
        )
    }

    private fun getLocationInfoUsingLatLng(lat: Double, lon: Double) {
        viewModel.getLocationInfoUsingLatLng(lat = lat, lon = lon)
    }

    private fun setZoomControl() {
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false
    }

    private fun setCurrentLocationIcon() {
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.current_location)
    }

    private fun setCameraFinishedListener() {
        naverMap.addOnCameraIdleListener {
            val centerLatLng = getCenterPosition()
            if (::currentLocation.isInitialized) {
                getLocationInfoUsingLatLng( //코스를 다 그린 후에도 계속 통신이 돌아서 리소스 낭비를 막기 위한 조치 필요
                    lat = centerLatLng.latitude, lon = centerLatLng.longitude
                )
            }
            Timber.tag("카메라-끝").d("$centerLatLng") //위에 통신이 비동기로 돌아서 이게 먼저 찍힘.
        }
    }

    private fun getCenterPosition(): LatLng {
        val cameraPosition = naverMap.cameraPosition
        return cameraPosition.target // 중심 좌표
    }

    private fun setLocationChangedListener() {
        naverMap.addOnLocationChangeListener { location ->
            currentLocation = LatLng(location.latitude, location.longitude)

            naverMap.locationOverlay.position = currentLocation
            naverMap.locationOverlay.isVisible = false
            setDepartureLatLng(latLng = LatLng(currentLocation.latitude, currentLocation.longitude))

            //같은 scope 안에 넣었으니 setDepartureLatLng 다음에 drawCourse가 실행되는 것이 보장됨
            //이때 isFirstInit의 초기값을 true로 줘서 최초 1회는 실행되게 하고 이후 drawCourse 내에서 isFirstInit 값을 false로 바꿔줌
            //뒤의 조건을 안 달아주면 다른 mode에서는 버튼을 클릭하기도 전에 drawCourse()가 돌 거라 안 됨.
            if (isFirstInit && isCurrentLocationMode) {
                drawCourse(departureLatLng = departureLatLng)
            }
        }
    }

    private fun setLocationTrackingMode() {
        naverMap.locationSource = locationSource

        if (isCurrentLocationMode || isCustomLocationMode) {
            naverMap.locationTrackingMode =
                LocationTrackingMode.Follow //위치추적 모드 Follow - 자동으로 camera 이동
        }
    }

    private fun courseFinish() {
        binding.btnDraw.setOnClickListener {
            if (isVisitorMode) {
                showRequireLoginDialog()
                return@setOnClickListener
            }
            requireCourseNameDialog().show()
        }
    }

    private fun requireCourseNameDialog(): BottomSheetDialog {
        val bottomSheetBinding = BottomsheetRequireCourseNameBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root
        val etCourseName = bottomSheetBinding.etCourseName
        val btnCreateCourse = bottomSheetBinding.btnCreateCourse

        etCourseName.addTextChangedListener {
            val isCourseNameValid = !it.isNullOrEmpty()

            with(btnCreateCourse) {
                setBackgroundResource(if (isCourseNameValid) R.drawable.radius_10_m1_button else R.drawable.radius_10_g3_button)
                isEnabled = isCourseNameValid
            }

            viewModel.courseTitle = if (isCourseNameValid) it.toString() else ""
            Timber.tag("EditTextValue").d(viewModel.courseTitle)
        }

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        btnCreateCourse.setOnClickListener {
            this.let {
                PermissionUtil.requestLocationPermission(
                    it, {
                        hideKeyboard(etCourseName)
                        bottomSheetDialog.dismiss()
                        createMBR()
                    },
                    { showPermissionDeniedToast() }, PermissionUtil.PermissionType.LOCATION
                )
            }
        }

        return bottomSheetDialog
    }

    private fun showPermissionDeniedToast() {
        showToast(getString(R.string.location_permission_denied))
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
            tvGuide.isVisible = false
            tvCustomDepartureGuideFrame.isVisible = false

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
            } else binding.tvPlaceName.text = buildingName

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
                val courseData = CourseData(
                    courseId = viewModel.uploadResult.value?.data?.id,
                    publicCourseId = null,
                    touchList = touchList,
                    startLatLng = departureLatLng,
                    departure = viewModel.departureName.value,
                    distance = viewModel.distanceSum.value,
                    image = captureUri.toString(),
                    dataFrom = "fromDrawCourse"
                )
                if (courseData.courseId == null || courseData.departure == null || courseData.distance == null) {
                    Toast.makeText(
                        this@DrawActivity,
                        ERROR_COURSE_NULL,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val intent = Intent(this@DrawActivity, CountDownActivity::class.java).apply {
                        putExtra(EXTRA_COURSE_DATA, courseData)
                    }
                    startActivity(intent)
                }
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

    private fun showRequireLoginDialog() {
        RequireLoginDialogFragment().show(supportFragmentManager, TAG_REQUIRE_LOGIN_DIALOG)
    }

    private fun backButton() {
        binding.imgBtnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun cameraUpdate(location: Any) {
        //맨 처음 지도 켤 때 startLocation으로 위치 옮길 때 사용
        if (location is LatLng) {
            val cameraUpdate = CameraUpdate.scrollTo(location).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        }
        //카메라 이동해서 캡쳐할 때 사용
        else if (location is LatLngBounds) {
            val cameraUpdate = CameraUpdate.fitBounds(location)
            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun drawCourse(departureLatLng: LatLng) {
        isFirstInit = false
        createDepartureMarker(departureLatLng = departureLatLng)
        createRouteMarker()
        deleteRouteMarker()
    }

    private fun createDepartureMarker(departureLatLng: LatLng) {
        setDepartureMarker(departureLatLng = departureLatLng)
        addDepartureToCoords(departureLatLng = departureLatLng)
        addDepartureToCalcDistanceList(departureLatLng = departureLatLng)
    }

    private fun setDepartureMarker(departureLatLng: LatLng) {
        val departureMarker = Marker()
        departureMarker.position = LatLng(departureLatLng.latitude, departureLatLng.longitude)
        departureMarker.anchor = PointF(0.5f, 0.5f)
        departureMarker.icon = OverlayImage.fromResource(R.drawable.runnect_marker)
        departureMarker.map = naverMap

        if (isSearchLocationMode) {
            cameraUpdate(
                LatLng(departureLatLng.latitude, departureLatLng.longitude)
            ) // 현위치에서 출발할 때 이것 때문에 트랙킹 모드 활성화 시 카메라 이동하는 게 묻혔음
        }
        setCustomInfoWindow(marker = departureMarker)
    }

    private fun setCustomInfoWindow(marker: Marker) {
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.ViewAdapter() {
            override fun getView(p0: InfoWindow): View {
                return LayoutInflater.from(this@DrawActivity)
                    .inflate(R.layout.custom_info_window, binding.root as ViewGroup, false)
            }
        }
        infoWindow.open(marker)
    }

    private fun addDepartureToCoords(departureLatLng: LatLng) {
        coords.add(LatLng(departureLatLng.latitude, departureLatLng.longitude))
    }

    private fun addDepartureToCalcDistanceList(departureLatLng: LatLng) {
        calcDistanceList.add(
            LatLng(
                departureLatLng.latitude, departureLatLng.longitude
            )
        )
    }

    private fun createRouteMarker() {
        naverMap.setOnMapClickListener { _, coord ->
            if (!isMarkerAvailable) return@setOnMapClickListener
            viewModel.isBtnAvailable.value = true

            if (touchList.size < MAX_MARKER_NUM) {
                addCoordsToTouchList(coord)
                setRouteMarker(coord)
                generateRouteLine(coord)
                calcDistance()
            } else {
                Toast.makeText(this, NOTIFY_LIMIT_MARKER_NUM, Toast.LENGTH_SHORT).show()
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

    /**
     * MBR : 남서쪽과 북동쪽 꼭지점 두 개의 좌표로 만드는 직사각형 영역
     */
    private fun createMBR() {
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

        when {
            isSearchLocationMode -> {
                viewModel.departureAddress.value = searchResult.fullAddress
                viewModel.departureName.value = searchResult.name
            }

            isCurrentLocationMode || isCustomLocationMode -> {
                viewModel.departureAddress.value =
                    viewModel.reverseGeocodingResult.value?.fullAddress
            }
        }

    }

    // Get uri of images from camera function
    private fun getImageUri(inImage: Bitmap): Uri {
        val tempFile = File.createTempFile("CreatedCourse", ".png")
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

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val TAG_REQUIRE_LOGIN_DIALOG = "REQUIRE_LOGIN_DIALOG"

        const val MAX_MARKER_NUM = 20
        const val DISTANCE_UNIT = "kilometer"
        const val LEAST_CONDITION_CREATE_PATH = 2
        const val EXTRA_SEARCH_RESULT = "searchResult"
        const val EXTRA_COURSE_DATA = "CourseData"
        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        const val CUSTOM_DEPARTURE = "내가 설정한 출발지"
        const val NOTIFY_LIMIT_MARKER_NUM = "마커는 20개까지 생성 가능합니다"

        const val ERROR_COURSE_NULL = "Error: Course data is incomplete"
    }
}