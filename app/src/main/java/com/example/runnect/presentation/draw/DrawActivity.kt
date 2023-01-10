package com.example.runnect.presentation.draw

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.databinding.ActivityDrawBinding
import com.example.runnect.presentation.search.entity.LocationLatLngEntity
import com.example.runnect.presentation.search.entity.SearchResultEntity
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class DrawActivity : BindingActivity<ActivityDrawBinding>(R.layout.activity_draw),
    OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val touchList = mutableListOf<LatLng>()
    private val markerList = mutableListOf<Marker>()

    private lateinit var searchResult: SearchResultEntity

    val distanceList = mutableListOf<LatLng>()//거리 계산용 list
    var sumList = mutableListOf<Double>()//Double

    val testNumber = 33.2380283028392
    val testNumberMade = BigDecimal(testNumber).setScale(1, RoundingMode.FLOOR)

    //지금 어디 코드 시점부터 넘어온 data를 받는 건지 정확한 파악이 안 됨. 그래서 받아온 data가 필요한 코드들을
    //draw()에다 싹 넣어줬는데 너무 길어짐. 그래서 일단 정리는 나중에 하기로 하고
    //별도의 전역 변수를 만들어서 쓰기로 함. 거리계산

    lateinit var startLatLngPublic: LocationLatLngEntity

    val viewModel: DrawViewModel by viewModels()

    private fun countDown() {
        binding.btnDraw.setOnClickListener {
            startActivity(Intent(this, CountDownActivity::class.java))
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
                countDown()

            }
        }
//        Timber.tag(ContentValues.TAG).d("searchResult@ : ${searchResult}")

    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

//        // 시작 지점 설정
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
//        naverMap.moveCamera(cameraUpdate)


        //네이버 맵 sdk에 위치 정보 제공
        locationSource = FusedLocationSource(this@DrawActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        drawCourse()

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false

    }


//    private fun cameraUpdate(location: LatLng) {
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
//            .animate(CameraAnimation.Easing)
//        naverMap.moveCamera(cameraUpdate)
//
//    }

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
        // 바로 위에 있는 startLatLng을 바로 전역변수로 선언하니까 데이터가 없다고 에러 떠서 일단 이 안에 냅두고 전역변수를 따로 만들어서
        // 값을 세팅해줬음. 나중에 정리해야 함.
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

        //여기에 거리 계산용 list 출발 지점 추가하는 코드 넣어야 할듯?
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
            Timber.tag(ContentValues.TAG).d("testNumberMade : ${testNumberMade}")
            Timber.tag(ContentValues.TAG).d("distanceList : ${distanceList}")
            Timber.tag(ContentValues.TAG).d("sumList : ${sumList}")
            Timber.tag(ContentValues.TAG)
                .d("viewModel_distanceSum : ${viewModel.distanceSum.value}")

        } //lineMarker-end

        //backButton
        binding.btnMarkerBack.setOnClickListener {
            if (touchList.size > 0) { // 이건 터치 좌표 리스트에 아무것도 없는데 버튼이 눌려서 npe가 뜨는 걸 방지하기 위함
                touchList.removeLast()
                Timber.tag(ContentValues.TAG).d("markerList : ${markerList.size}")
                markerList.last().map = null
                markerList.removeLast()
                Timber.tag(ContentValues.TAG).d("markerList : ${markerList.size}")


                coords.removeLast()
                if (coords.size >= 2) { //백버튼이 눌리면 coords 사이즈도 하나씩 줄여가는데 (시작점 포함해서) 2개면 경로선 그리고
                    // 그 밑이 돼서 선을 아예 그릴 수 없어지면 null로 없애기.
                    path.coords = coords
                    path.map = naverMap
                } else {
                    path.map = null
                }

            }

            //백 버튼 눌렀을 때 거리 계산 다시
            distanceList.removeLast()
            sumList.removeLast()
            val test = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR)
            viewModel.distanceSum.value = test.toDouble() //거리 합을 뷰모델에 세팅
        }
    }


    private fun calculateDistance() {

        // 이게 맵에 터치가 일어날 때마다 for문이 처음부터 싹 다시 도는 게 함정임.

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


//근데 이러면 그런 문제는 있겠다. 우연히 좌표간 거리가 똑같은 게 여러개 있을 수 있는데 그럴 땐 거리 계산이 제대로 안 된다는 거.
            //소수점 버린다음 sumList에 추가를 하면 겹칠 확률이 높아지는데 가공 전에 넣는 거라 겹칠 일이 거의 없을 것 같긴함.
            //소수점이 한두자리가 아니라 엄청 많아서

            if (!sumList.contains(distanceResult)) {
                sumList.add(distanceResult)
                val test = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR)
                viewModel.distanceSum.value = test.toDouble() //거리 합을 뷰모델에 세팅
            }
        } // 거리 계산 for문 종료

    }


    private fun addListeners() {
        binding.btnDraw.setOnClickListener {
            createMbr()
        }
    }

    //모든 마커를 포함할 수 있도록 하는 꼭지점 좌표 두개를 만들고
    //중간지점의 좌표값을 구해서 카메라 위치를 이동할 수 있게 함.
    private fun createMbr() {
        val startLatLng = searchResult.locationLatLng
        val bounds = LatLngBounds.Builder()
            .include(LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble()))
            .include(touchList)
            .build()
        naverMap.setContentPadding(100, 100, 100, 100)
        cameraUpdate(bounds)

        naverMap.addOnCameraIdleListener {
            Toast.makeText(context, "카메라 움직임 종료", Toast.LENGTH_SHORT).show()
            captureMap()
        }

    }

    private fun captureMap() {
        //캡쳐해서 이미지 뷰에 set하기~
        naverMap.takeSnapshot {
            binding.ivDrawingCaptured.setImageBitmap(it)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { //권한 거부 시
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val SEARCH_RESULT_BEFORE_RUN = "SearchResultBeforeRun"

    }
}
