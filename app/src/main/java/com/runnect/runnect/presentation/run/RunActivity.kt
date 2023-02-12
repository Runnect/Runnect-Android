package com.runnect.runnect.presentation.run

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.runnect.runnect.R
import com.runnect.runnect.data.model.DetailToRunData
import com.runnect.runnect.data.model.DrawToRunData
import com.runnect.runnect.data.model.MyDrawToRunData
import com.runnect.runnect.data.model.RunToEndRunData
import com.runnect.runnect.data.model.entity.LocationLatLngEntity
import com.runnect.runnect.databinding.ActivityRunBinding
import com.runnect.runnect.presentation.endrun.EndRunActivity
import kotlinx.android.synthetic.main.custom_dialog_finish_run.view.*
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.concurrent.timer

class RunActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityRunBinding>(R.layout.activity_run),
    OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수
    private var currentLocation: LatLng = LatLng(37.52901832956373, 126.9136196847032) //국회의사당 좌표


    private val touchList = arrayListOf<LatLng>() //ArrayList<LatLng>() 하니까 x


    //타이머
    var time = 0
    var hour = 0//시간
    var minute = 0//분
    var second = time //초

    var timerTask: Timer? = null

    lateinit var timerSecond: String
    lateinit var timerMinute: String
    lateinit var timerHour: String


    val viewModel: RunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        init()
        startTimer()
        getCurrentLocation()
        seeRecord()


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

    private fun init() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this) //
        initView() //지도 뷰 표시
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0


        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Follow //위치추적 모드 Follow

        //네이버 맵 sdk에 위치 정보 제공
        locationSource = FusedLocationSource(this@RunActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        naverMap.addOnLocationChangeListener { location ->
            currentLocation = LatLng(location.latitude, location.longitude)
            map.locationOverlay.run { //현재 위치 마커
                isVisible = true //현재 위치 마커 가시성(default = false)
                position = LatLng(currentLocation.latitude, currentLocation.longitude)
            }
        }
        drawCourse()

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false

        //현위치 커스텀 이미지
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.ic_location_overlay)

    }

    //카메라 위치 변경 함수
    private fun cameraUpdate(location: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

    }

    private fun getCurrentLocation() {
        binding.btnCurrentLocation.setOnClickListener {
            cameraUpdate(currentLocation)
        }
    }

    private fun drawCourse() {


        val intent: Intent = intent

        val drawToRunData: DrawToRunData? = intent.getParcelableExtra("DrawToRunData")
        val myDrawToRunData: MyDrawToRunData? = intent.getParcelableExtra("myDrawToRun")
        val detailToRunData: DetailToRunData? = intent.getParcelableExtra("detailToRun")


        if (drawToRunData == null) {//myDraw or detail
            if(myDrawToRunData == null){
                //detail 코드 세팅
                for (i in 1..detailToRunData!!.path.size - 1) {
                    touchList.add(LatLng(detailToRunData.path[i][0],
                        detailToRunData.path[i][1])) //서버에서 보내주는 건 LatLng이 아니라 Double이라서 받아온 걸 다시 LatLng으로 감싸줘야함.
                }

                val distanceCut =
                    BigDecimal(detailToRunData.distance.toDouble()).setScale(1, RoundingMode.FLOOR)
                        .toDouble()

                viewModel.courseId.value = detailToRunData.courseId
                viewModel.publicCourseId.value = detailToRunData.publicCourseId
                viewModel.touchList.value = touchList //출발 지점을 뺀 path가 필요한데 detailToRunData는 포함돼있어서 직접 만들어줌. removeAt()이런 걸 쓰는 방향으로 리팩토링하면 좋을 듯함.
                viewModel.distanceSum.value = distanceCut
                viewModel.departure.value = detailToRunData.departure
                viewModel.captureUri.value = detailToRunData.image
                viewModel.startLatLng.value = LocationLatLngEntity(detailToRunData.path[0][0].toFloat(),
                    detailToRunData.path[0][1].toFloat())
                Timber.tag(ContentValues.TAG).d("detailToRun : $detailToRunData")


            } else if (detailToRunData == null){
                //myDraw 코드 세팅

                for (i in 1..myDrawToRunData!!.path.size - 1) {
                    touchList.add(LatLng(myDrawToRunData.path[i][0],
                        myDrawToRunData.path[i][1])) //서버에서 보내주는 건 LatLng이 아니라 Double이라서 받아온 걸 다시 LatLng으로 감싸줘야함.
                }

                val distanceCut =
                    BigDecimal(myDrawToRunData.distance.toDouble()).setScale(1, RoundingMode.FLOOR)
                        .toDouble()

                viewModel.courseId.value = myDrawToRunData.courseId
                viewModel.publicCourseId.value = myDrawToRunData.publicCourseId

                viewModel.touchList.value =
                    touchList //출발 지점을 뺀 path가 필요한데 myDrawToRunData는 포함돼있어서 직접 만들어줌. removeAt()이런 걸 쓰는 방향으로 리팩토링하면 좋을 듯함.
                viewModel.distanceSum.value = distanceCut
                viewModel.departure.value = myDrawToRunData.departure
                viewModel.captureUri.value = myDrawToRunData.image
                viewModel.startLatLng.value = LocationLatLngEntity(myDrawToRunData.path[0][0].toFloat(),
                    myDrawToRunData.path[0][1].toFloat())
                Timber.tag(ContentValues.TAG).d("myDrawToRun : $myDrawToRunData")
            }


        } else if (drawToRunData != null) { //가독성을 위해 일부러 else가 아닌 else if를 써줌.

            //drawToRun 세팅

            val distanceCut =
                BigDecimal(drawToRunData.totalDistance!!.toDouble()).setScale(1, RoundingMode.FLOOR)
                    .toDouble()

            viewModel.courseId.value = drawToRunData.courseId
            viewModel.publicCourseId.value = drawToRunData.publicCourseId

            viewModel.distanceSum.value =
                distanceCut //앞에 drawToRunDta. 이 부분 변수처리 해놓고 .뒤에 딸려오는 변수명 맞춰준다음 .앞에 이름만 바꿔주면 코드 양 줄일 수 있을듯
            viewModel.departure.value = drawToRunData?.departure
            viewModel.captureUri.value = drawToRunData?.captureUri
            viewModel.startLatLng.value = drawToRunData?.startLatLng
            viewModel.touchList.value =
                drawToRunData?.touchList //이거 때문에 굳이 viewModel.touchList의 타입을 ArrayList<LatLng>으로 해준 것.
            Timber.tag(ContentValues.TAG).d("drawToRunData : $drawToRunData")
        } // 세팅 종료


        val viewModelStartLatLng =
            viewModel.startLatLng.value // 아래 startLatLng에 바로 안 들어가져서 따로 변수를 만들어서 넣어줌
        Timber.tag(ContentValues.TAG).d("viewModelStartLatLng : ${viewModel.startLatLng.value}")

        val path = PathOverlay()
        //startMarker-start
        val startMarker = Marker()

        val startLatLng =
            LatLng(viewModelStartLatLng!!.latitude.toDouble(),
                viewModelStartLatLng!!.longitude.toDouble())

        startMarker.position =
            LatLng(startLatLng.latitude, startLatLng.longitude) // 출발지점
        startMarker.anchor = PointF(0.5f, 0.7f)
        startMarker.icon = OverlayImage.fromResource(R.drawable.startmarker)
        startMarker.map = naverMap

        cameraUpdate(
            LatLng(startLatLng.latitude, startLatLng.longitude)
        )

        val coords = mutableListOf(
            LatLng(startLatLng.latitude, startLatLng.longitude)
        )
        //startMarker-end


        //lineMarker-start
        val marker = Marker()

        //여기가 그 forEach 때문에 마지막에만 마커 찍히는 부분.
        //DrawActivity에서는 터치 리스너가 있어서 객체를 여러개 만들어 줄 수 있었는데 여기는 직접 for문 돌려줘야 될듯.

        val viewModelTouchList =
            viewModel.touchList.value

        viewModelTouchList?.forEach { touch ->
            marker.position = LatLng(touch.latitude, touch.longitude)
            marker.anchor = PointF(0.5f, 0.5f)
            marker.icon = OverlayImage.fromResource(R.drawable.marker_line)
            marker.map = naverMap

            // 경로선 list인 coords에 터치로 받아온 좌표값을 추가
            coords.add(LatLng(touch.latitude, touch.longitude))

            // 경로선 그리기

            path.coords = coords

            Timber.tag(ContentValues.TAG).d("pat.coords : ${path.coords}")

            // 경로선 색상
            path.color = Color.parseColor("#593EEC")
            // 경로선 테두리 색상
            path.outlineColor = Color.parseColor("#593EEC")
            path.map = naverMap
        }

        //lineMarker-end


    }

    private fun seeRecord() {
        binding.btnRunFinish.setOnClickListener {
            stopTimer()
            bottomSheet()

        }
    }

    @SuppressLint("MissingInflatedId")
    fun bottomSheet() {
        // bottomSheetDialog 객체 생성
        val bottomSheetDialog = BottomSheetDialog(
            this@RunActivity, R.style.BottomSheetDialogTheme
        )
        // layout_bottom_sheet를 뷰 객체로 생성
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.custom_dialog_finish_run,
            findViewById(R.id.bottomSheet) as LinearLayout?
        )
        // bottomSheetDialog의 dismiss 버튼 선택시 dialog disappear
        bottomSheetView.findViewById<View>(R.id.btn_see_record).setOnClickListener {
            val intent = Intent(this@RunActivity, EndRunActivity::class.java).apply {

                putExtra("RunToEndRunData",
                    RunToEndRunData(
                        courseId = viewModel.courseId.value!!, publicCourseId = viewModel.publicCourseId.value,
                        viewModel.distanceSum.value,
                        viewModel.captureUri.value,
                        viewModel.departure.value,
                        timerHour,
                        timerMinute,
                        timerSecond))

                addFlags(FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거

            }
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }
        // bottomSheetDialog 뷰 생성
        bottomSheetDialog.setContentView(bottomSheetView)
        // bottomSheetDialog 호출
        bottomSheetDialog.show()
    }

    private fun startTimer() {
        timerTask = timer(period = 1000) {

            second++ //1초에 한 번씩 timer 값이 1씩 증가, 초기값은 0

            if (second == 60) {
                second = 0
                minute += 1
            }
            if (minute == 60) {
                minute = 0
                hour += 1
            }

            timerSecond = second.toString() //intent로 넘길 값 전역변수에 세팅
            timerMinute = minute.toString()
            timerHour = hour.toString()

            runOnUiThread {
                binding.tvTimeRecord.text = "$hour : $minute : $second"
            }

        }
    }

    private fun stopTimer() {
        timerTask?.cancel()
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
