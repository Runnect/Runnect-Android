package com.runnect.runnect.presentation.endrun

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
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

//    lateinit var timeTotal : String
//    lateinit var paceTotal : String

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

//        Timber.tag(ContentValues.TAG).d("currentTime : ${dataFormat5.format(currentTime)}")

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
                UiState.Failure -> Timber.tag(ContentValues.TAG)
                    .d("Failure : ${viewModel.errorMessage.value}")
            }
        }


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

        val courseId = runToEndRunData.courseId
        viewModel.courseId.value = courseId

        val publicCourseId = runToEndRunData.publicCourseId
        viewModel.publicCourseId.value = publicCourseId

        val captureUri = runToEndRunData.captureUri!!.toUri()
        viewModel.captureUri.value = captureUri

        //String으로 다룰 거면 그냥 시간/분/초 합쳐서 관리하는 게 나을듯?
        val timerHour = runToEndRunData.timerHour
        viewModel.timerHour.value = timerHour //뷰모델에 타이머 sec 세팅

        val timerMinute = runToEndRunData.timerMinute
        viewModel.timerMinute.value = timerMinute //뷰모델에 타이머 Milli 세팅

        val timerSecond = runToEndRunData.timerSecond
        viewModel.timerSecond.value = timerSecond //뷰모델에 타이머 Milli 세팅

        Glide
            .with(binding.ivEndRunCapture.context)
            .load(captureUri)
            .centerCrop()
            .into(binding.ivEndRunCapture)

        val pace1 = BigDecimal(timerSecond!!.toDouble() / totalDistance.toDouble()).setScale(2,
            RoundingMode.FLOOR).toString()
        val pace2 = BigDecimal(timerSecond!!.toDouble() / totalDistance.toDouble()).setScale(2,
            RoundingMode.FLOOR).toString()
        val pace3 = BigDecimal(timerSecond!!.toDouble() / totalDistance.toDouble()).setScale(2,
            RoundingMode.FLOOR).toString() //서버에서 요구하는 형식에 맞춰주기 위함

        viewModel.timeTotal.value = "$timerHour:$timerMinute:$timerSecond"
        viewModel.paceTotal.value = "$pace1:$pace2:$pace3"



        binding.tvDepartureRecord.text = departure //추후에 data binding으로 리팩토링
        binding.tvDistanceData.text = totalDistance
        binding.tvTimeData.text = "${viewModel.timeTotal.value}"
        binding.tvPaceData.text = "${viewModel.paceTotal.value}"
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
            Timber.tag(ContentValues.TAG)
                .d("viewModel.courseId.value!! : ${viewModel.courseId.value!!}")
            Timber.tag(ContentValues.TAG)
                .d("viewModel.courseId.value!! : ${viewModel.publicCourseId.value}")
            Timber.tag(ContentValues.TAG)
                .d("viewModel.courseId.value!! : ${viewModel.editTextValue.value!!}")
            Timber.tag(ContentValues.TAG)
                .d("viewModel.courseId.value!! : ${viewModel.timeTotal.value!!}")
            Timber.tag(ContentValues.TAG)
                .d("viewModel.courseId.value!! : ${viewModel.paceTotal.value!!}")

            viewModel.postRecord(
                RequestPostRecordDto(viewModel.courseId.value!!,
                    viewModel.publicCourseId.value,
                    viewModel.editTextValue.value!!,
                    viewModel.timeTotal.value!!,
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