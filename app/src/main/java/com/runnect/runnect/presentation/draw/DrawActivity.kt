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
import com.runnect.runnect.data.model.UploadLatLng
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


    private lateinit var searchResult: SearchResultEntity

    val distanceList = arrayListOf<LatLng>()//?????? ????????? list
    val distanceListtoUpload =
        arrayListOf<UploadLatLng>()
    var sumList = mutableListOf<Double>()//Double


    lateinit var captureUri: Uri

    var markerAvailable: Boolean = false


    lateinit var startLatLngPublic: LatLng
    var distancePublic by Delegates.notNull<Float>()

    val viewModel: DrawViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = intent.getParcelableExtra("searchResult")
                    ?: throw Exception("???????????? ???????????? ????????????.")

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

        //MapFragment ??????
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this) //?????? ?????? ????????????
        locationSource = FusedLocationSource(
            this,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0


        //????????? ??? sdk??? ?????? ?????? ??????
        locationSource = FusedLocationSource(this@DrawActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        drawCourse()

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false


    }


    private fun courseFinish() {
        binding.btnDraw.setOnClickListener {
            createMbr()
            Handler(Looper.getMainLooper()).postDelayed( //?????? ??? ???????????? ??? ?????? ??????
                {
                    viewModel.uploadCourse()
                }, 300
            )

        }
    }

    private fun goToDraw() { //?????? ????????? ????????????????????? ????????? ???????????? ?????? ??? ????????????
        binding.btnPreStart.setOnClickListener {
            markerAvailable = true //?????? ????????? ?????? ??? ??????!
            val animDown = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
            val animUp = AnimationUtils.loadAnimation(this, R.anim.slide_out_up)

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

                viewBottomSheetFrame.isVisible =
                    false //frame??? invisible?????? ????????? ????????? constraint??? ???????????? ????????? ??? ?????????
                tvPlaceName.isVisible = false
                tvPlaceAddress.isVisible = false
                btnPreStart.isVisible = false


                //DrawActivity Component visibile
                btnDraw.isVisible = true
                btnMarkerBack.isVisible = true
                frameCourseDistance.isVisible = true
                tvCourseDistanceRecord.isVisible = true
                tvCourseDistanceKm.isVisible = true


            }


        }
    }

    private fun addObserver() {
        //markerAvailable??? ????????? ?????? ?????? ?????? ??? ????????? ??????????????? ?????? ????????? ??? ????????? ?????? ??????
        //btnAvailable??? ????????????/????????? ????????? ???????????? ????????? ????????? ?????? ???. ???????????? ???????????? ????????? ?????? ????????? ?????? ???.

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
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible ???????????? ???????????? ??? ??????
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
        dialog.setCancelable(false) // ?????? ?????? ?????? ??????
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // ?????? ??? layout ?????? ?????? ?????? ??????
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
                    viewModel.distanceSum.value!!,
                    searchResult.name,
                    captureUri.toString()))

            Timber.tag(ContentValues.TAG).d("departure ?????? : ${searchResult.name}")
            Timber.tag(ContentValues.TAG).d("captureUri ?????? : ${captureUri}")

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


    //????????? ?????? ?????? ??????
    private fun cameraUpdate(location: Any) {
        //?????? ??? ?????? ?????? ??? ??? startLocation?????? ?????? ?????? ??? ??????
        if (location is LatLng) {
            val cameraUpdate = CameraUpdate.scrollTo(location)
            naverMap.moveCamera(cameraUpdate)
        }
        //?????? ????????? ???????????? ????????? ??? ??????
        else if (location is LatLngBounds) {
            val cameraUpdate = CameraUpdate.fitBounds(location)
            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun drawCourse() {
        val path = PathOverlay()
        //startMarker-start
        val startMarker = Marker()
        val startLatLng = searchResult.locationLatLng //????????? ????????? data??? ????????? ??????
        startLatLngPublic = LatLng(startLatLng.latitude.toDouble(),
            startLatLng.longitude.toDouble())

        startMarker.position =
            LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble()) // ????????????
        startMarker.anchor = PointF(0.5f, 0.7f)
        startMarker.icon = OverlayImage.fromResource(R.drawable.start_marker)
        startMarker.map = naverMap

        cameraUpdate(
            LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble())
        )

        val coords = mutableListOf(
            LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble())
        )
        //startMarker-end

        //?????? ????????? list ?????? ?????? ???????????? ??????
        distanceList.add(
            LatLng(
                startLatLngPublic.latitude.toDouble(),
                startLatLngPublic.longitude.toDouble()
            )
        )


        //lineMarker-start
        //??? ?????? ????????? ??????
        naverMap.setOnMapClickListener { point, coord ->
            // ????????? ???????????? touchList??? ??????
            if (markerAvailable == true) { //??? ?????? ????????? ???????????? ?????? ??????
                viewModel.btnAvailable.value = true
                if (touchList.size < 20) { // 20???????????? ?????? ??????????????? ????????? ???????????? ??????


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


                    // ????????? list??? coords??? ????????? ????????? ???????????? ??????
                    coords.add(LatLng("${coord.latitude}".toDouble(),
                        "${coord.longitude}".toDouble()))

                    // ????????? ?????????

                    path.coords = coords

                    Timber.tag(ContentValues.TAG).d("pat.coords : ${path.coords}")

                    // ????????? ??????
                    path.color = Color.parseColor("#593EEC")
                    // ????????? ????????? ??????
                    path.outlineColor = Color.parseColor("#593EEC")
                    path.map = naverMap

                    //????????? ??? ????????? ?????? ??????
                    calculateDistance()
                    Timber.tag(ContentValues.TAG).d("distanceList : ${distanceList}")
                    Timber.tag(ContentValues.TAG).d("sumList : ${sumList}")
                    Timber.tag(ContentValues.TAG)
                        .d("viewModel_distanceSum : ${viewModel.distanceSum.value}")
                } else {
                    Toast.makeText(this, "????????? 20????????? ?????? ???????????????", Toast.LENGTH_SHORT).show()
                }
            }


        } //lineMarker-end

        //backButton
        binding.btnMarkerBack.setOnClickListener {
            //???????????? ????????? ?????? ????????? ????????? ??? ????????? ?????? ????????? ?????????. ????????? ??? false??? ????????? ?????????...
            touchList.removeLast()
            Timber.tag(ContentValues.TAG).d("markerList : ${markerList.size}")
            markerList.last().map = null
            markerList.removeLast()
            Timber.tag(ContentValues.TAG).d("markerList : ${markerList.size}")

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


            //??? ?????? ????????? ??? ?????? ?????? ??????
            if (distanceList.size > 0 && sumList.size > 0) {
                distanceList.removeLast()
                sumList.removeLast()
            }
            val test = BigDecimal(sumList.sum()).setScale(1, RoundingMode.FLOOR).toFloat()
            viewModel.distanceSum.value = test //?????? ?????? ???????????? ??????
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
                viewModel.distanceSum.value = test //?????? ?????? ???????????? ??????
                distancePublic = test //?????? ????????? ??????
            }
        } // ?????? ?????? for??? ??????

    }

    //?????? ????????? ????????? ??? ????????? ?????? ????????? ?????? ????????? ?????????
//??????????????? ???????????? ????????? ????????? ????????? ????????? ??? ?????? ???.
    private fun createMbr() {
        val startLatLng = startLatLngPublic
        val bounds = LatLngBounds.Builder()
            .include(LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble()))
            .include(touchList)
            .build()
        naverMap.setContentPadding(100, 100, 100, 100)
        cameraUpdate(bounds)
        captureMap()

        for (i in 1..distanceList.size) {
            distanceListtoUpload.add(
                UploadLatLng(
                    distanceList[i - 1].latitude,
                    distanceList[i - 1].longitude
                )
            )
        }

        viewModel.path.value = distanceListtoUpload
        Timber.tag(ContentValues.TAG).d("?????????path : ${viewModel.path.value}")
        //distanceSum??? ??? ?????? ?????? ???????????? ??? ??????????????? ????????? ?????????
        viewModel.departureAddress.value = searchResult.fullAdress
        Timber.tag(ContentValues.TAG).d("?????????departureAddress : ${viewModel.departureAddress.value}")
        viewModel.departureName.value = searchResult.name
        Timber.tag(ContentValues.TAG).d("?????????departureName : ${viewModel.departureName.value}")


    }

    private fun captureMap() {
        naverMap.takeSnapshot { // intent??? ?????? ?????? ????????? ????????? data ??????
            val captureUri = getImageUri(this@DrawActivity, it)

            //Bitmap -> Uri
            Timber.tag(ContentValues.TAG).d("??????it : ${it}")
            Timber.tag(ContentValues.TAG).d("??????uri : ${captureUri}")

            viewModel.setRequestBody(
                ContentUriRequestBody(
                    this,
                    captureUri
                )
            ) //Uri -> RequestBody
            Timber.tag(ContentValues.TAG).d("???????????????img : ${viewModel.image.value}")
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
        captureUri = uri // intent??? ?????? ??????????????? uri ??????
        return uri
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

    }
}