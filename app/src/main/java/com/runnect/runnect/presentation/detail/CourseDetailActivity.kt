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
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.model.DetailToRunData
import com.runnect.runnect.data.model.MyDrawToRunData
import com.runnect.runnect.data.model.ResponseGetMyDrawDetailDto
import com.runnect.runnect.data.model.RunToEndRunData
import com.runnect.runnect.databinding.ActivityCourseDetailBinding
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.discover.DiscoverFragment
import com.runnect.runnect.presentation.endrun.EndRunActivity
import com.runnect.runnect.presentation.report.ReportActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.setEditBottomSheet
import com.runnect.runnect.util.extension.setEditBottomSheetClickListener
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_edit_mode.*
import timber.log.Timber

@AndroidEntryPoint
class CourseDetailActivity :
    BindingActivity<ActivityCourseDetailBinding>(R.layout.activity_course_detail) {
    private val viewModel: CourseDetailViewModel by viewModels()
    private var courseId: Int = 0
    private var root: String = ""
    lateinit var departureLatLng : LatLng
    private val touchList = arrayListOf<LatLng>()
    private lateinit var editBottomSheet: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseId = intent.getIntExtra("courseId", 0)
        root = intent.getStringExtra("root").toString()
        addListener()
        initView()
        getCourseDetail()
        addObserver()
        initEditBottomSheet()
    }



    private fun initView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
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
            val intent = Intent(this, CountDownActivity::class.java).apply {
                putExtra("detailToRun",
                DetailToRunData(
                    viewModel.courseDetail.courseId,
                    viewModel.courseDetail.id,
                    touchList,
                    departureLatLng,
                    viewModel.courseDetail.departure,
                    viewModel.courseDetail.distance.toFloat(),
                    viewModel.courseDetail.image
                ))
            }
            startActivity(intent)
        }
        binding.btnShowMore.setOnClickListener {
            if(root == "upload"){
                editBottomSheet.show()
            }
            else {
                bottomSheet()
            }
        }
    }

    private fun getCourseDetail(){
        viewModel.getCourseDetail(courseId)
    }

    private fun setDepartureLatLng() {
        departureLatLng =
            LatLng(viewModel.courseDetail.path[0][0], viewModel.courseDetail.path[0][1])
    }

    private fun setTouchList() {
        for (i in 1 until viewModel.courseDetail.path.size) {
            touchList.add(LatLng(viewModel.courseDetail.path[i][0], viewModel.courseDetail.path[i][1]))
        }
    }


    private fun addObserver() {
        viewModel.courseDetailState.observe(this) { state ->
            if (state == UiState.Success) {
                with(binding) {
                    with(viewModel.courseDetail) {
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

                setDepartureLatLng()
                setTouchList()
            }
        }
    }

    private fun initEditBottomSheet() {
        editBottomSheet = setEditBottomSheet()
        setEditBottomSheetClickEvent()
    }

    private fun setEditBottomSheetClickEvent() {
        editBottomSheet.setEditBottomSheetClickListener { which ->
            when (which) {
                editBottomSheet.layout_edit_frame -> {
                    showToast("수정하기")
                }
                editBottomSheet.layout_delete_frame -> {
                    showToast("삭제하기")
                }
            }
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