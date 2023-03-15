package com.runnect.runnect.presentation.draw

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.runnect.runnect.BuildConfig
import com.runnect.runnect.R
import com.runnect.runnect.data.model.DrawToRunData
import com.runnect.runnect.data.model.entity.SearchResultEntity
import com.runnect.runnect.databinding.ActivityDrawBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.ContentUriRequestBody
import kotlinx.android.synthetic.main.custom_dialog_make_course.view.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.properties.Delegates

class DrawActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityDrawBinding>(R.layout.activity_draw),
    OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val touchList = arrayListOf<LatLng>()
    private val markerList = mutableListOf<Marker>()

    lateinit var departureLatLng: LatLng
    val coords = mutableListOf<LatLng>()
    val path = PathOverlay()
    lateinit var animDown: Animation
    lateinit var animUp: Animation
    var distanceSum: Float = 0.0f


    private lateinit var searchResult: SearchResultEntity

    val distanceList = arrayListOf<LatLng>()//거리 계산용 list
    var sumList = mutableListOf<Double>()//Double


    lateinit var captureUri: Uri
    var isMarkerAvailable: Boolean = false
    var distancePublic by Delegates.notNull<Float>()
    val viewModel: DrawViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = intent.getParcelableExtra("searchResult")
                    ?: throw Exception("데이터가 존재하지 않습니다.")

                Timber.tag(ContentValues.TAG).d("searchResult : ${searchResult}")
                viewModel.searchResult.value = searchResult
                initView()
                courseFinish()
                addObserver()
                backButton()
                goToDraw()
            }
        }

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun initView() {

        //MapFragment 추가
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this) //지도 객체 얻어오기
        locationSource = FusedLocationSource(
            this,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0


        //네이버 맵 sdk에 위치 정보 제공
        locationSource = FusedLocationSource(this@DrawActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        drawCourse()

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false


    }


    private fun courseFinish() {
        binding.btnDraw.setOnClickListener {
            createMbr()
            Handler(Looper.getMainLooper()).postDelayed( //이거 왜 콜백으로 안 되는 거지
                {
                    viewModel.uploadCourse()
                }, 300
            )

        }
    }

    private fun goToDraw() { //버튼 누르면 애니메이션으로 위아래 컴포넌트 일단 다 제거되게
        binding.btnPreStart.setOnClickListener {
            isMarkerAvailable = true
            hideDeparture()
            showDrawCourse()
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
        //isMarkerAvailable은 출발지 설정 전에 마커 못 만들게 막아놨는데 버튼 누르면 그 제한을 푸는 거고
        //btnAvailable은 완성하기/백버튼 활성화 비활성화 컨트롤 하려고 만든 거. 변수명이 명확하지 않아서 고칠 필요는 있을 듯.

        viewModel.btnAvailable.observe(this) {
            if (viewModel.btnAvailable.value == true) {
                binding.btnMarkerBack.isEnabled = true
                binding.btnMarkerBack.setImageResource(R.drawable.backcourse_enable_true)

                binding.btnDraw.isEnabled = true
                binding.btnDraw.setBackgroundResource(R.drawable.radius_10_m1_button)
            } else {
                binding.btnMarkerBack.isEnabled = false
                binding.btnMarkerBack.setImageResource(R.drawable.backcourse_enable_false)

                binding.btnDraw.isEnabled = false
                binding.btnDraw.setBackgroundResource(R.drawable.radius_10_g3_button)
            }

        }

        viewModel.drawState.observe(this) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    customDialog(binding.root)
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                }
            }
        }


    }

    fun customDialog(view: View) {
        val myLayout = layoutInflater.inflate(R.layout.custom_dialog_make_course, null)

        val build = AlertDialog.Builder(view.context).apply {
            setView(myLayout)
        }
        val dialog = build.create()
        dialog.setCancelable(false) // 외부 영역 터치 금지
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 내가 짠 layout 외의 영역 투명 처리
        dialog.show()

        myLayout.btn_storage.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("fromDrawActivity", true)
            }
            startActivity(intent)
            dialog.dismiss()
        }
        myLayout.btn_run.setOnClickListener {
            val intent = Intent(this, CountDownActivity::class.java)

            intent.putExtra("DrawToRunData",
                DrawToRunData(
                    courseId = viewModel.courseId.value!!,
                    publicCourseId = null,
                    touchList,
                    departureLatLng,
                    viewModel.distanceSum.value!!,
                    searchResult.name,
                    captureUri.toString()))

            startActivity(intent)
            dialog.dismiss()
        }

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
            val cameraUpdate = CameraUpdate.scrollTo(location)
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

    private fun createDepartureMarker() {
        val departureMarker = Marker()
        departureLatLng = LatLng(searchResult.locationLatLng.latitude.toDouble(),
            searchResult.locationLatLng.longitude.toDouble())

        departureMarker.position =
            LatLng(departureLatLng.latitude, departureLatLng.longitude) // 출발지점
        departureMarker.anchor = PointF(0.5f, 0.7f)
        departureMarker.icon = OverlayImage.fromResource(R.drawable.marker_departure)
        departureMarker.map = naverMap

        cameraUpdate(
            LatLng(departureLatLng.latitude, departureLatLng.longitude)
        )

        coords.add(LatLng(departureLatLng.latitude, departureLatLng.longitude))

        distanceList.add(
            LatLng(
                departureLatLng.latitude,
                departureLatLng.longitude
            )
        )
    }

    private fun createRouteMarker() {
        naverMap.setOnMapClickListener { point, coord ->
            if (isMarkerAvailable) {
                viewModel.btnAvailable.value = true
                if (touchList.size < 20) { // 20개까지만 생성 가능하도록 제한을 걸어주기 위함
                    touchList.add(
                        LatLng(
                            coord.latitude,
                            coord.longitude
                        )
                    )

                    val routeMarker = Marker()

                    routeMarker.position = LatLng(
                        coord.latitude,
                        coord.longitude
                    )
                    routeMarker.anchor = PointF(0.5f, 0.5f)
                    routeMarker.icon = OverlayImage.fromResource(R.drawable.marker_route)
                    routeMarker.map = naverMap

                    markerList.add(routeMarker)

                    coords.add(LatLng(coord.latitude, coord.longitude)) // coords에 터치로 받아온 좌표값 추가

                    path.coords = coords // 경로선 그리기
                    path.color = Color.parseColor("#593EEC") // 경로선 색상
                    path.outlineColor = Color.parseColor("#593EEC") // 경로선 테두리 색상
                    path.map = naverMap

                    calculateDistance()
                } else {
                    Toast.makeText(this, "마커는 20개까지 생성 가능합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteRouteMarker() {
        binding.btnMarkerBack.setOnClickListener {
            touchList.removeLast()
            markerList.last().map = null
            markerList.removeLast()

            if (touchList.isEmpty()) {
                viewModel.btnAvailable.value = false
            }
            coords.removeLast()
            if (coords.size >= 2) {
                path.coords = coords
                path.map = naverMap
            } else {
                path.map = null
            }

            //백 버튼 눌렀을 때 거리 계산 다시
            if (distanceList.size > 0 && sumList.size > 0) {
                distanceList.removeLast()
                sumList.removeLast()
            }
            distanceSum = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR).toFloat()
            viewModel.distanceSum.value = distanceSum //거리 합을 뷰모델에 세팅
        }
    }


    private fun calculateDistance() {

        for (i in 1..touchList.size) {
            if (!distanceList.contains(touchList[i - 1])) {
                distanceList.add(touchList[i - 1])
            }

        }

        for (num in 0..distanceList.size - 2) {
            val distanceResult = viewModel.distance(
                distanceList[num].latitude,
                distanceList[num].longitude,
                distanceList[num + 1].latitude,
                distanceList[num + 1].longitude,
                "kilometer"
            )

            if (!sumList.contains(distanceResult)) {
                sumList.add(distanceResult)
                distanceSum = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR).toFloat()
                viewModel.distanceSum.value = distanceSum //거리 합을 뷰모델에 세팅
            }
        } // 거리 계산 for문 종료

    }

    //모든 마커를 포함할 수 있도록 하는 꼭지점 좌표 두개를 만들고
//중간지점의 좌표값을 구해서 카메라 위치를 이동할 수 있게 함.
    private fun createMbr() {
        val bounds = LatLngBounds.Builder()
            .include(LatLng(departureLatLng.latitude, departureLatLng.longitude))
            .include(touchList)
            .build()
        naverMap.setContentPadding(100, 100, 100, 100)
        cameraUpdate(bounds)
        captureMap()


        viewModel.path.value = distanceList
        Timber.tag(ContentValues.TAG).d("뷰모델path : ${viewModel.path.value}")
        //distanceSum은 calculateDistance()에서 뷰모델에 값 갱신되도록 세팅을 해줬음
        viewModel.departureAddress.value = searchResult.fullAdress
        Timber.tag(ContentValues.TAG).d("뷰모델departureAddress : ${viewModel.departureAddress.value}")
        viewModel.departureName.value = searchResult.name
        Timber.tag(ContentValues.TAG).d("뷰모델departureName : ${viewModel.departureName.value}")


    }

    private fun captureMap() {
        naverMap.takeSnapshot { // intent로 넘길 전역 변수에 비트맵 data 넣음
            val captureUri = getImageUri(this@DrawActivity, it)

            //Bitmap -> Uri
            Timber.tag(ContentValues.TAG).d("캡쳐it : ${it}")
            Timber.tag(ContentValues.TAG).d("캡쳐uri : ${captureUri}")

            viewModel.setRequestBody(
                ContentUriRequestBody(
                    this,
                    captureUri
                )
            ) //Uri -> RequestBody
            Timber.tag(ContentValues.TAG).d("뷰모델캡쳐img : ${viewModel.image.value}")
        }
    }

    // Get uri of images from camera function
    private fun getImageUri(inContext: Context?, inImage: Bitmap): Uri {

        val tempFile = File.createTempFile("temprentpk", ".png")
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        val uri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".fileprovider", tempFile
        )
        captureUri = uri // intent로 넘길 전역변수에 uri 세팅
        return uri
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

    }
}