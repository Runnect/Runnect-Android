package com.runnect.runnect.presentation.endrun

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.runnect.runnect.R
import com.runnect.runnect.data.model.RunToEndRunData
import com.runnect.runnect.databinding.ActivityEndRunBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.util.extension.clearFocus
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat

class EndRunActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityEndRunBinding>(R.layout.activity_end_run) {


    val viewModel: EndRunViewModel by viewModels()

    val currentTime: Long = System.currentTimeMillis() // ms로 반환

    lateinit var runToEndRunData: RunToEndRunData

    @SuppressLint("SimpleDateFormat")
    val dataFormat5 = SimpleDateFormat("yyyy.MM.dd")

    //    val dataFormat5 = SimpleDateFormat("현재시각은 yyyy-MM-dd hh:mm:ss")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        backBtn()
        editTextController()
        getIntentValue()
        saveButton()

//        Timber.tag(ContentValues.TAG).d("currentTime : ${dataFormat5.format(currentTime)}")

        viewModel.currentTime.value = dataFormat5.format(currentTime)

    }

    fun backBtn() {
        binding.imgBtnBack.setOnClickListener {
            finish()
        }
    }

    fun getIntentValue() {
        //DrawToRunData나 DetailToRunData나 RunActivity에서 RunToEndRunData로 가공이 되기 때문에 RunActivity와는 상황이 다름
        runToEndRunData =
            intent.getParcelableExtra("RunToEndRunData")!!

        Timber.tag(ContentValues.TAG).d("runToEndRunData : $runToEndRunData")

        val totalDistance = runToEndRunData.totalDistance.toString()
//        viewModel.distanceSum.value = totalDistance as Double? // 뷰모델에 총거리 세팅

        val departure = runToEndRunData.departure
//        viewModel.departure.value = departure // 뷰모델에 출발지 세팅

        val captureUri = runToEndRunData.captureUri!!.toUri()
        viewModel.captureUri.value = captureUri

        val timerSec = runToEndRunData.timerSec
        viewModel.timerSec.value = timerSec //뷰모델에 타이머 sec 세팅

        val timerMilli = runToEndRunData.timerMilli
        viewModel.timerMilli.value = timerMilli //뷰모델에 타이머 Milli 세팅

        Glide
            .with(binding.ivEndRunCapture.context)
            .load(captureUri)
            .centerCrop()
            .into(binding.ivEndRunCapture)

        val pace = BigDecimal(timerSec!!.toDouble() / totalDistance.toDouble()).setScale(2,
            RoundingMode.FLOOR).toDouble()
        binding.tvDepartureRecord.text = departure //추후에 data binding으로 리팩토링
        binding.tvDistanceData.text = totalDistance
        binding.tvTimeData.text = "$timerSec : $timerMilli"
        binding.tvPaceData.text = pace.toString()
    }

    private fun editTextController() {
        binding.etTitleCourse.addTextChangedListener {
            if (!binding.etTitleCourse.text.isNullOrEmpty()) {
                viewModel.editTextValue.value = binding.etTitleCourse.text.toString()
                binding.btnEndRunSave.setBackgroundResource(R.drawable.radius_10_m1_button)
                binding.btnEndRunSave.isEnabled = true
                Timber.tag(ContentValues.TAG)
                    .d("editText.value : ${viewModel.editTextValue.value}")

            } else {
                binding.btnEndRunSave.setBackgroundResource(R.drawable.radius_10_g3_button)
                binding.btnEndRunSave.isEnabled = false

            }
        }
    }


    private fun saveButton() {
        binding.btnEndRunSave.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
            startActivity(intent)
        }
    }

    //키보드 밖 터치 시, 키보드 내림
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev!!.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                clearFocus(focusView)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}