package com.runnect.runnect.presentation.detail

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.model.RunToEndRunData
import com.runnect.runnect.databinding.ActivityCourseDetailBinding
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.discover.DiscoverFragment
import com.runnect.runnect.presentation.endrun.EndRunActivity
import com.runnect.runnect.presentation.report.ReportActivity
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CourseDetailActivity :
    BindingActivity<ActivityCourseDetailBinding>(R.layout.activity_course_detail) {
    private val viewModel: CourseDetailViewModel by viewModels()
    private var courseId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseId = intent.getIntExtra("courseId", 0)//선택한 코스의 id로 API 호출 예정
        Timber.tag(ContentValues.TAG).d("상세페이지로 넘겨받은 courseId 값? : $courseId")
        addListener()
        initView()
        addObserver()
        viewModel.getCourseDetail(courseId)
        showReportBottomSheet()
    }

    private fun initView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun onBackPressed() {
        val intent = Intent(this, DiscoverFragment::class.java)
        setResult(RESULT_OK, intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun addListener() {
        binding.ivCourseDetailBack.setOnClickListener {
            val intent = Intent(this, DiscoverFragment::class.java)
            setResult(RESULT_OK, intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.ivCourseDetailScrap.setOnClickListener {
            it.isSelected = !it.isSelected
            viewModel.postCourseScrap(id = courseId, it.isSelected)
        }
        binding.btnCourseDetailFinish.setOnClickListener {
            val intent = Intent(this, CountDownActivity::class.java)
            //API 수정의 요지가 있어 추후 협의를 거친 후 달리기 시작 추가 예정
            //0407 지금 상세페이지로 오긴 했는데 여기서
        }
    }

    private fun addObserver() {
        viewModel.courseDetailState.observe(this) { state ->
            if (state == UiState.Success) {
                with(binding) {
                    with(viewModel.courseDetail) {
                        Timber.tag(ContentValues.TAG).d("화면에 바인딩할 image 값? : $image")
                        ivCourseDetailMap.load(image)
                        ivCourseDetailProfileStamp.load(stampId)
                        ivCourseDetailProfileNickname.text = nickname
                        tvCourseDetailProfileLv.text = level
                        tvCourseDetailTitle.text = title
                        tvCourseDetailDistance.text = distance
                        tvCourseDetailDeparture.text = departure
                        tvCourseDetailDesc.text = description
                        ivCourseDetailScrap.isSelected = scrap
                    }

                }
            }
        }
    }

    private fun showReportBottomSheet(){
        binding.btnShowMore.setOnClickListener {
            bottomSheet()
        }
    }

    @SuppressLint("MissingInflatedId")
    fun bottomSheet() {
        // bottomSheetDialog 객체 생성
        val bottomSheetDialog = BottomSheetDialog(
            this@CourseDetailActivity, R.style.BottomSheetDialogTheme
        )

        // layout_bottom_sheet를 뷰 객체로 생성
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.custom_dialog_report,
            findViewById<LinearLayout>(R.id.bottomSheet)
        )
        // bottomSheetDialog의 dismiss 버튼 선택시 dialog disappear
        bottomSheetView.findViewById<View>(R.id.view_go_to_report_frame).setOnClickListener {
            val intent = Intent(this@CourseDetailActivity, ReportActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
            }
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }
        // bottomSheetDialog 뷰 생성
        bottomSheetDialog.setContentView(bottomSheetView)
        // bottomSheetDialog 호출
        bottomSheetDialog.show()
    }
}