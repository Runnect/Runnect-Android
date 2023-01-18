package com.runnect.runnect.presentation.endrun

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.runnect.runnect.R
import com.runnect.runnect.databinding.ActivityEndRunBinding
import com.runnect.runnect.presentation.coursemain.CourseMainActivity
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat

class EndRunActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityEndRunBinding>(R.layout.activity_end_run) {


    val viewModel: EndRunViewModel by viewModels()

    val currentTime : Long = System.currentTimeMillis() // ms로 반환

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



        Timber.tag(ContentValues.TAG).d("currentTime : ${dataFormat5.format(currentTime)}")

        viewModel.currentTime.value = dataFormat5.format(currentTime)

    }

    fun backBtn() {
        binding.imgBtnBack.setOnClickListener {
            finish()
        }
    }

    fun getIntentValue() {
        val totalDistance = intent.getSerializableExtra("totalDistance").toString() //총거리
//        viewModel.distanceSum.value = totalDistance as Double? // 뷰모델에 총거리 세팅
        Timber.tag(ContentValues.TAG).d("totalDistance 로그 : ${totalDistance}")

        val departure = intent.getStringExtra("departure")!! //출발지
//        viewModel.departure.value = departure // 뷰모델에 출발지 세팅
        Timber.tag(ContentValues.TAG).d("departure 로그 : ${departure}")


        val captureUri = intent.getStringExtra("captureUri")!!.toUri() //이미지 uri
        viewModel.captureUri.value = captureUri //뷰모델에 이미지 uri 세팅
        Timber.tag(ContentValues.TAG).d("captureUri 로그 : ${captureUri}")

        val timerSec = intent.getStringExtra("timerSec")
        viewModel.timerSec.value = timerSec //뷰모델에 타이머 sec 세팅
        Timber.tag(ContentValues.TAG).d("timerSec 로그 : ${timerSec}")

        val timerMilli = intent.getStringExtra("timerMilli")
        viewModel.timerMilli.value = timerMilli //뷰모델에 타이머 Milli 세팅
        Timber.tag(ContentValues.TAG).d("timerMilli 로그 : ${timerMilli}")

        Glide
            .with(binding.ivEndRunCapture.context)
            .load(captureUri)
            .centerCrop()
            .into(binding.ivEndRunCapture)

        val pace = BigDecimal(timerSec!!.toDouble() / totalDistance.toDouble()).setScale(2,
            RoundingMode.FLOOR).toDouble()
        binding.tvDepartureRecord.text = departure
        binding.tvDistanceData.text = totalDistance
        binding.tvTimeData.text = "${timerSec} : ${timerMilli}"
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
            val intent = Intent(this, CourseMainActivity::class.java)
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

    //키보드 내리기(포커스 해제) 확장함수
    fun Context.clearFocus(view: View) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}