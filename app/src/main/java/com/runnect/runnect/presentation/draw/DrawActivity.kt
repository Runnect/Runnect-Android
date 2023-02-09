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
import androidx.activity.viewModels
import androidx.core.content.FileProvider
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
import com.runnect.runnect.data.model.UploadLatLng
import com.runnect.runnect.data.model.entity.LocationLatLngEntity
import com.runnect.runnect.data.model.entity.SearchResultEntity
import com.runnect.runnect.databinding.ActivityDrawBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
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


    private lateinit var searchResult: SearchResultEntity

    val distanceList = arrayListOf<LatLng>()//거리 계산용 list
    val distanceListtoUpload =
        arrayListOf<UploadLatLng>()
    var sumList = mutableListOf<Double>()//Double


    lateinit var captureUri: Uri


    lateinit var startLatLngPublic: LocationLatLngEntity
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
                initView()
//                addListeners()

                courseFinish()
                backButton()


            }
        }

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

            //createMbr()이 분명 먼저 도는데 변환에서 속도가 걸리니까 늦게 찍혀 그래서 핸들러를 써줌
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    for (i in 1..distanceList.size) {
                        distanceListtoUpload.add(
                            UploadLatLng(
                                distanceList[i - 1].latitude,
                                distanceList[i - 1].longitude
                            )
                        )
                    }

                    viewModel.path.value = distanceListtoUpload
                    //distanceSum은 딴 데서 이미 뷰모델에 값 갱신되도록 세팅을 해줬음
                    viewModel.departureAddress.value = searchResult.fullAdress
                    viewModel.departureName.value = searchResult.name

                    Timber.tag(ContentValues.TAG).d("viewModel.path : ${viewModel.path.value}")
                    Timber.tag(ContentValues.TAG)
                        .d("viewModel.distance : ${viewModel.distanceSum.value}")
                    Timber.tag(ContentValues.TAG)
                        .d("viewModel.departureAddress : ${viewModel.departureAddress.value}")
                    Timber.tag(ContentValues.TAG)
                        .d("viewModel.departureName : ${viewModel.departureName.value}")
                    Timber.tag(ContentValues.TAG).d("viewModel.image : ${viewModel.image.value}")
                }, 600
            )

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    viewModel.uploadCourse()

                    viewModel.errorMessage.observe(this) {
                    }
                    viewModel.uploadResult.observe(this) {
                        viewModel.courseId.value = it.data.course.id

                    }

                }, 800
            )


            customDialog(binding.root)
        }
    }

    fun customDialog(view: View) {
        val myLayout = layoutInflater.inflate(R.layout.custom_dialog_make_course, null)

        val build = AlertDialog.Builder(view.context).apply {
            setView(myLayout)
        }
        val dialog = build.create()
//        dialog.setCancelable(false) // 외부 영역 터치 금지
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
                    startLatLngPublic,
                    viewModel.distanceSum.value,
                    searchResult.name,
                    captureUri.toString()))

            Timber.tag(ContentValues.TAG).d("departure 로그 : ${searchResult.name}")
            Timber.tag(ContentValues.TAG).d("captureUri 로그 : ${captureUri}")

            startActivity(intent)
            dialog.dismiss()
        }

    }

    private fun backButton() {
        binding.imgBtnBack.setOnClickListener {
            finish()
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
        val path = PathOverlay()
        //startMarker-start
        val startMarker = Marker()
        val startLatLng = searchResult.locationLatLng
        startLatLngPublic = startLatLng

        startMarker.position =
            LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble()) // 출발지점
        startMarker.anchor = PointF(0.5f, 0.7f)
        startMarker.icon = OverlayImage.fromResource(R.drawable.startmarker)
        startMarker.map = naverMap

        cameraUpdate(
            LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble())
        )

        val coords = mutableListOf(
            LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble())
        )
        //startMarker-end

        //거리 계산용 list 출발 지점 추가하는 코드
        distanceList.add(
            LatLng(
                startLatLngPublic.latitude.toDouble(),
                startLatLngPublic.longitude.toDouble()
            )
        )

        //lineMarker-start
        //맵 터치 이벤트 관리
        naverMap.setOnMapClickListener { point, coord ->
            // 수신한 좌표값을 touchList에 추가
            touchList.add(
                LatLng(
                    "${coord.latitude}".toDouble(),
                    "${coord.longitude}".toDouble()
                )
            )

            val marker = Marker()

            marker.position = LatLng(
                "${coord.latitude}".toDouble(),
                "${coord.longitude}".toDouble()
            )
            marker.anchor = PointF(0.5f, 0.5f)
            marker.icon = OverlayImage.fromResource(R.drawable.marker_line)
            marker.map = naverMap

            markerList.add(marker)

            Timber.tag(ContentValues.TAG).d("markerListSize : ${markerList.size}")
            Timber.tag(ContentValues.TAG).d("markerList : ${markerList}")


            // 경로선 list인 coords에 터치로 받아온 좌표값을 추가
            coords.add(LatLng("${coord.latitude}".toDouble(), "${coord.longitude}".toDouble()))

            // 경로선 그리기

            path.coords = coords

            Timber.tag(ContentValues.TAG).d("pat.coords : ${path.coords}")

            // 경로선 색상
            path.color = Color.parseColor("#593EEC")
            // 경로선 테두리 색상
            path.outlineColor = Color.parseColor("#593EEC")
            path.map = naverMap

            //터치가 될 때마다 거리 계산
            calculateDistance()
            Timber.tag(ContentValues.TAG).d("distanceList : ${distanceList}")
            Timber.tag(ContentValues.TAG).d("sumList : ${sumList}")
            Timber.tag(ContentValues.TAG)
                .d("viewModel_distanceSum : ${viewModel.distanceSum.value}")

        } //lineMarker-end

        //backButton
        binding.btnMarkerBack.setOnClickListener {
            if (touchList.size > 0) { // 이건 터치 좌표 리스트에 아무것도 없는데 버튼이 눌려서 NPE가 뜨는 걸 방지하기 위함
                touchList.removeLast()
                Timber.tag(ContentValues.TAG).d("markerList : ${markerList.size}")
                markerList.last().map = null
                markerList.removeLast()
                Timber.tag(ContentValues.TAG).d("markerList : ${markerList.size}")


                coords.removeLast()
                if (coords.size >= 2) {
                    path.coords = coords
                    path.map = naverMap
                } else {
                    path.map = null
                }

            }

            //백 버튼 눌렀을 때 거리 계산 다시
            if (distanceList.size > 0 && sumList.size > 0) {
                distanceList.removeLast()
                sumList.removeLast()
            }
            val test = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR).toFloat()
            viewModel.distanceSum.value = test //거리 합을 뷰모델에 세팅
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
                val test = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR).toFloat()
                viewModel.distanceSum.value = test //거리 합을 뷰모델에 세팅
                distancePublic = test //전역 변수에 세팅
            }
        } // 거리 계산 for문 종료

    }

    //모든 마커를 포함할 수 있도록 하는 꼭지점 좌표 두개를 만들고
//중간지점의 좌표값을 구해서 카메라 위치를 이동할 수 있게 함.
    private fun createMbr() {
        val startLatLng = startLatLngPublic
        val bounds = LatLngBounds.Builder()
            .include(LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble()))
            .include(touchList)
            .build()
        naverMap.setContentPadding(100, 100, 100, 100)
        cameraUpdate(bounds)
        captureMap()


    }

    private fun captureMap() {
        naverMap.takeSnapshot { // intent로 넘길 전역 변수에 비트맵 data 넣음
            val captureUri = getImageUri(this@DrawActivity, it)

            //Bitmap -> Uri
            Timber.tag("캡쳐it").d("${it}")
            Timber.tag("캡쳐uri").d("${captureUri}")

            viewModel.setRequestBody(
                ContentUriRequestBody(
                    this,
                    captureUri
                )
            ) //Uri -> RequestBody
            Timber.tag("캡쳐").d("${viewModel.image.value}")
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
