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
import kotlin.math.roundToInt

class EndRunActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityEndRunBinding>(R.layout.activity_end_run) {

    val viewModel: EndRunViewModel by viewModels()
    val currentTime: Long = System.currentTimeMillis()

    lateinit var runToEndRunData: RunToEndRunData

    var paceFull: Double = 0.0
    var paceMinute: Int = 0
    var paceSecond: Int = 0

    lateinit var timerHour: String
    lateinit var timerMinute: String
    lateinit var timerSecond: String

    var transferHourToMinute: Double = 0.0
    var transferSecondToMinute: Double = 0.0
    var totalMinute: Double = 0.0

    @SuppressLint("SimpleDateFormat")
    val dataFormat5 = SimpleDateFormat("yyyy.MM.dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        backBtn()
        editTextController()
        getIntentValue()
        setTimerViewModelValue()
        transferMinuteForCalcPace()
        setPaceViewModelValue()
        saveRecord()
        addObserver()
        setCurrentTimeCustomFormat()

    }

    private fun backBtn() {
        binding.imgBtnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("dataFrom", viewModel.dataFrom.value)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun editTextController() {
        binding.etTitleCourse.addTextChangedListener {
            if (!binding.etTitleCourse.text.isNullOrEmpty()) {

                viewModel.editTextValue.value = binding.etTitleCourse.text.toString()
                Timber.tag(ContentValues.TAG).d("editText.value : ${viewModel.editTextValue.value}")

                enableSaveBtn()
                notifyMaxTitleLength()

            } else {
                disableSaveBtn()
            }
        }
    }

    private fun enableSaveBtn() {
        binding.btnEndRunSave.setBackgroundResource(R.drawable.radius_10_m1_button)
        binding.btnEndRunSave.isEnabled = true
    }

    private fun disableSaveBtn() {
        binding.btnEndRunSave.setBackgroundResource(R.drawable.radius_10_g3_button)
        binding.btnEndRunSave.isEnabled = false
    }

    private fun notifyMaxTitleLength() {
        if (binding.etTitleCourse.text.length == 20) {
            Toast.makeText(this, "최대 20자까지 입력 가능합니다", Toast.LENGTH_SHORT).show()
        }
    }

    fun getIntentValue() {
        runToEndRunData = intent.getParcelableExtra("RunToEndRunData")!!

        Timber.tag(ContentValues.TAG).d("runToEndRunData : $runToEndRunData")

        viewModel.distanceSum.value = runToEndRunData.totalDistance
        viewModel.departure.value = runToEndRunData.departure
        viewModel.courseId.value = runToEndRunData.courseId
        viewModel.publicCourseId.value = runToEndRunData.publicCourseId
        viewModel.dataFrom.value = runToEndRunData.dataFrom
        val captureUri = runToEndRunData.captureUri!!.toUri()
        viewModel.captureUri.value = captureUri
    }

    //각각의 시간/분/초가 한자리수로 넘어올 때 형식 가공
    private fun setTimerViewModelValue() {
        timerHour = runToEndRunData.timerHour.toString()
        timerMinute = runToEndRunData.timerMinute.toString()
        timerSecond = runToEndRunData.timerSecond.toString()

        if (timerSecond.length == 1) {
            timerSecond = "0${timerSecond}"
        }

        if (timerMinute.length == 1) {
            timerMinute = "0${timerMinute}"
        }

        if (timerHour.length == 1) {
            timerHour = "0${timerHour}"
        }
        viewModel.timerHourMinSec.value = "$timerHour:$timerMinute:$timerSecond"
    }

    //평균페이스는 '분'을 기준으로 표기하기 때문에 시간, 초를 '분'으로 변환해주어야 함
    private fun transferMinuteForCalcPace() {
        transferHourToMinute = timerHour.toDouble() * 60
        transferSecondToMinute = timerSecond.toDouble() / 60
        totalMinute = transferHourToMinute + timerMinute.toInt() + transferSecondToMinute


        Timber.tag(ContentValues.TAG).d("transferHourToMinute 값 : $transferHourToMinute")
        Timber.tag(ContentValues.TAG).d("transferSecondToMinute 값 : $transferSecondToMinute")
        Timber.tag(ContentValues.TAG).d("totalMinute 값 : $totalMinute")
    }

    // 평균 페이스는 '분 / 거리'로 나온 값을 다음과 같이 표기해야 한다. ex) 18.20 -> 18'20"
    private fun setPaceViewModelValue() {
        paceFull = BigDecimal(totalMinute / runToEndRunData.totalDistance!!).setScale(
            2, RoundingMode.FLOOR
        ).toDouble()

        paceMinute = BigDecimal(totalMinute / runToEndRunData.totalDistance!!).setScale(
            0, RoundingMode.FLOOR
        ).toInt() // ex) 18.20 -> 18

        paceSecond = ((paceFull - paceMinute) * 100).roundToInt() // ex) 0.20 ->20

        viewModel.paceTotal.value = "$paceMinute'$paceSecond\""
    }

    private fun saveRecord() {
        binding.btnEndRunSave.setOnClickListener {
            viewModel.postRecord(
                RequestPostRecordDto(
                    viewModel.courseId.value!!,
                    viewModel.publicCourseId.value,
                    viewModel.editTextValue.value!!,
                    viewModel.timerHourMinSec.value!!,
                    "0:$paceMinute:$paceSecond" //평균 페이스 표기법에서는 '시간'을 안 쓰지만 서버에서 요구하는 형식에 맞춰주기 위해 앞에 "0:"을 붙어야 함
                )
            )
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }
    }

    private fun addObserver() {
        viewModel.endRunState.observe(this) {
            when (it) {
                UiState.Empty -> hideLoadingBar()
                UiState.Loading -> showLoadingBar()
                UiState.Success -> {
                    hideLoadingBar()
                    notifyUploadFinish()
                }
                UiState.Failure -> {
                    hideLoadingBar()
                    showErrorMessage()
                }
            }
        }
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun notifyUploadFinish() {
        showToast("저장한 러닝 기록은 마이페이지에서 볼 수 있어요")
        Timber.tag(ContentValues.TAG).d("서버 성공 : ${viewModel.uploadResult.value!!.message}")
    }

    private fun showErrorMessage() {
        Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
    }

    private fun setCurrentTimeCustomFormat() {
        viewModel.currentTime.value = dataFormat5.format(currentTime)
    }


    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("dataFrom", viewModel.dataFrom.value)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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