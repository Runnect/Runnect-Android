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

    val currentTime: Long = System.currentTimeMillis() // ms로 반환

    lateinit var runToEndRunData: RunToEndRunData
    var paceMinute: Int = 0
    var paceSecond: Int = 0

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
        saveRecord()
        addObserver()

        viewModel.currentTime.value = dataFormat5.format(currentTime)

    }

    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    private fun notifyUploadFinish() {
        showToast("업로드 완료!")

        Timber.tag(ContentValues.TAG)
            .d("서버 성공 : ${viewModel.uploadResult.value!!.message}")
    }

    private fun showErrorMessage() {
        Timber.tag(ContentValues.TAG)
            .d("Failure : ${viewModel.errorMessage.value}")
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

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("dataFrom", viewModel.dataFrom.value)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //CLEAR TOP이랑 CLEAR TASK랑 무슨 차이야야        }
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun backBtn() {
        binding.imgBtnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("dataFrom", viewModel.dataFrom.value)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //CLEAR TOP이랑 CLEAR TASK랑 무슨 차이야야        }
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    fun getIntentValue() {
        runToEndRunData =
            intent.getParcelableExtra("RunToEndRunData")!!

        Timber.tag(ContentValues.TAG).d("runToEndRunData : $runToEndRunData")

        //뷰모델에 값 세팅
        viewModel.distanceSum.value = runToEndRunData.totalDistance
        viewModel.departure.value = runToEndRunData.departure
        viewModel.courseId.value = runToEndRunData.courseId
        viewModel.publicCourseId.value = runToEndRunData.publicCourseId
        viewModel.dataFrom.value = runToEndRunData.dataFrom
        val captureUri = runToEndRunData.captureUri!!.toUri()
        viewModel.captureUri.value = captureUri

        //형식 가공을 위해 toString으로 바꿔줌
        var timerHour = runToEndRunData.timerHour.toString()
        var timerMinute = runToEndRunData.timerMinute.toString()
        var timerSecond = runToEndRunData.timerSecond.toString()
        Timber.tag(ContentValues.TAG).d("timerHour 값 : ${timerHour}")
        Timber.tag(ContentValues.TAG).d("timerMinute 값 : ${timerMinute}")
        Timber.tag(ContentValues.TAG).d("timerSecond 값 : ${timerSecond}")

        //각각의 시간/분/초가 한자리수로 넘어올 때 형식 가공
        if(timerSecond.length == 1){
            timerSecond = "0${timerSecond}"
        }

        if(timerMinute.length == 1){
            timerMinute = "0${timerMinute}"
        }

        if(timerHour.length == 1){
            timerHour = "0${timerHour}"
        }

        //가공한 형식을 '이동시간' TextView에 바인딩
        viewModel.timerHourMinSec.value = "$timerHour:$timerMinute:$timerSecond"


        //평균페이스 Logic
        val transferHourToMinute: Double = timerHour.toDouble() * 60 //'시간'을 '분'으로 환산
        val transferSecondToMinute: Double = timerSecond.toDouble() / 60  //'초'를 '분'으로 환산
        val totalMinute = transferHourToMinute + timerMinute.toInt() + transferSecondToMinute


        Timber.tag(ContentValues.TAG).d("transferHourToMinute 값 : $transferHourToMinute")
        Timber.tag(ContentValues.TAG).d("transferSecondToMinute 값 : $transferSecondToMinute")
        Timber.tag(ContentValues.TAG).d("totalMinute 값 : $totalMinute")


        val paceFull =
            BigDecimal(totalMinute / runToEndRunData.totalDistance!!).setScale(2,
                RoundingMode.FLOOR).toDouble() // 평균 페이스 표기 법 ex) 18.20 -> 이걸 18'20"

        paceMinute = BigDecimal(totalMinute / runToEndRunData.totalDistance!!).setScale(0,
            RoundingMode.FLOOR).toInt() // ex) 18

        paceSecond = ((paceFull - paceMinute)*100).roundToInt() // ex) 0.20 ->20

        //가공한 형식을 '평균 페이스' TextView에 바인딩
        viewModel.paceTotal.value = "$paceMinute'$paceSecond\""
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

    private fun editTextController() {
        binding.etTitleCourse.addTextChangedListener {
            if (!binding.etTitleCourse.text.isNullOrEmpty()) {

                viewModel.editTextValue.value = binding.etTitleCourse.text.toString()
                Timber.tag(ContentValues.TAG)
                    .d("editText.value : ${viewModel.editTextValue.value}")

                enableSaveBtn()
                notifyMaxTitleLength()

            } else {
                disableSaveBtn()
            }
        }
    }


    private fun saveRecord() {
        binding.btnEndRunSave.setOnClickListener {

            viewModel.postRecord(
                RequestPostRecordDto(viewModel.courseId.value!!,
                    viewModel.publicCourseId.value,
                    viewModel.editTextValue.value!!,
                    viewModel.timerHourMinSec.value!!,
                    "0:$paceMinute:$paceSecond")
                //평균 페이스 표기법에서는 '시간'을 안 쓰지만 서버에서 요구하는 형식에 맞춰주려면 앞에 0:을 붙여줘야 함
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