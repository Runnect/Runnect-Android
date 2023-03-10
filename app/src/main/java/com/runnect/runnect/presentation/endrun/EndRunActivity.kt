package com.runnect.runnect.presentation.endrun

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.runnect.runnect.R
import com.runnect.runnect.data.model.RequestPostRecordDto
import com.runnect.runnect.data.model.RunToEndRunData
import com.runnect.runnect.databinding.ActivityEndRunBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.clearFocus
import com.runnect.runnect.util.extension.showToast
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
        addObserver()

        viewModel.currentTime.value = dataFormat5.format(currentTime)

    }

    private fun addObserver() {
        viewModel.endRunState.observe(this) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    showToast("업로드 완료!")
                    Timber.tag(ContentValues.TAG)
                        .d("서버 성공 : ${viewModel.uploadResult.value!!.message}")
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }


    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun backBtn() {
        binding.imgBtnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    fun getIntentValue() {
        runToEndRunData =
            intent.getParcelableExtra("RunToEndRunData")!!

        Timber.tag(ContentValues.TAG).d("runToEndRunData : $runToEndRunData")

        viewModel.distanceSum.value = runToEndRunData.totalDistance // 뷰모델에 총거리 세팅
        viewModel.departure.value = runToEndRunData.departure // 뷰모델에 출발지 세팅
        viewModel.courseId.value = runToEndRunData.courseId
        viewModel.publicCourseId.value = runToEndRunData.publicCourseId

        val captureUri = runToEndRunData.captureUri!!.toUri()
        viewModel.captureUri.value = captureUri

        val timerHour = runToEndRunData.timerHour
        val timerMinute = runToEndRunData.timerMinute
        val timerSecond = runToEndRunData.timerSecond

        viewModel.timerHourMinSec.value = "$timerHour:$timerMinute:$timerSecond"

        val pace =
            BigDecimal(runToEndRunData.totalDistance!! / runToEndRunData.timeTotal!!).setScale(1,
                RoundingMode.FLOOR).toString()

        viewModel.paceTotal.value = "$pace:$pace:$pace" //서버에서 요구하는 형식에 맞춰주기 위함 HH:MM:SS
        // 이 부분 api 수정돼야하지 않을까? 페이스 표기가 6'85" 이런식으로 되는데 HH:MM:SS로 받으면 어떡해
    }

    private fun editTextController() {
        binding.etTitleCourse.addTextChangedListener {
            if (!binding.etTitleCourse.text.isNullOrEmpty()) {
                viewModel.editTextValue.value = binding.etTitleCourse.text.toString()
                binding.btnEndRunSave.setBackgroundResource(R.drawable.radius_10_m1_button)
                binding.btnEndRunSave.isEnabled = true
                Timber.tag(ContentValues.TAG)
                    .d("editText.value : ${viewModel.editTextValue.value}")

                if (binding.etTitleCourse.text.length == 20) {
                    Toast.makeText(this, "최대 20자까지 입력 가능합니다", Toast.LENGTH_SHORT).show()
                }


            } else {
                binding.btnEndRunSave.setBackgroundResource(R.drawable.radius_10_g3_button)
                binding.btnEndRunSave.isEnabled = false

            }
        }
    }


    private fun saveButton() {
        binding.btnEndRunSave.setOnClickListener {

            viewModel.postRecord(
                RequestPostRecordDto(viewModel.courseId.value!!,
                    viewModel.publicCourseId.value,
                    viewModel.editTextValue.value!!,
                    viewModel.timerHourMinSec.value!!,
                    viewModel.paceTotal.value!!)
            )
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