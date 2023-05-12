package com.runnect.runnect.presentation.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.application.ApplicationClass
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.model.DetailToRunData
import com.runnect.runnect.databinding.ActivityCourseDetailBinding
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.discover.DiscoverFragment
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.report.ReportActivity
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_require_login.view.*
import timber.log.Timber

@AndroidEntryPoint
class CourseDetailActivity :
    BindingActivity<ActivityCourseDetailBinding>(R.layout.activity_course_detail) {
    private val viewModel: CourseDetailViewModel by viewModels()
    private var courseId: Int = 0

    lateinit var departureLatLng: LatLng
    private val touchList = arrayListOf<LatLng>()

    var isVisitorMode: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkVisitorMode()

        courseId = intent.getIntExtra("courseId", 0)
        Timber.tag(ContentValues.TAG).d("상세페이지로 넘겨받은 courseId 값? : $courseId")
        addListener()
        initView()
        getCourseDetail()
        addObserver()
        showReportBottomSheet()
    }

    private fun checkVisitorMode() {
        isVisitorMode =
            PreferenceManager.getString(ApplicationClass.appContext, "access")!! == "visitor"
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

            if (isVisitorMode) {
                requireVisitorLogin(binding.root)
            } else {
                val intent = Intent(this, CountDownActivity::class.java).apply {
                    putExtra(
                        "detailToRun",
                        DetailToRunData(
                            viewModel.courseDetail.courseId,
                            viewModel.courseDetail.id,
                            touchList,
                            departureLatLng,
                            viewModel.courseDetail.departure,
                            viewModel.courseDetail.distance.toFloat(),
                            viewModel.courseDetail.image
                        )
                    )
                }
                startActivity(intent)
            }
        }
    }

    private fun requireVisitorLogin(view: View) {
        val myLayout = layoutInflater.inflate(R.layout.custom_dialog_require_login, null)

        val build = AlertDialog.Builder(view.context).apply {
            setView(myLayout)
        }
        val dialog = build.create()
        dialog.setCancelable(false) // 외부 영역 터치 금지
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 내가 짠 layout 외의 영역 투명 처리
        dialog.show()

        myLayout.btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        myLayout.btn_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }
    }

    private fun getCourseDetail() {
        viewModel.getCourseDetail(courseId)
    }

    //set이란 단어가 표현력이 떨어지는 것 같기도 하고. 그래서 일단 뭉탱이로 두는 것보단 쪼개는 게 나아서 쪼개놓음
    private fun setDepartureLatLng() {
        departureLatLng =
            LatLng(viewModel.courseDetail.path[0][0], viewModel.courseDetail.path[0][1])
        Timber.tag(ContentValues.TAG).d("departureLatLng 값 : $departureLatLng")
    } //통신 결과가 세팅되기 전에 이 코드가 돌아버림. 그래서 뒤에 UiState가 success일 때 안으로 이 함수를 넣어줌

    private fun setTouchList() {
        for (i in 1 until viewModel.courseDetail.path.size) {
            touchList.add(
                LatLng(
                    viewModel.courseDetail.path[i][0],
                    viewModel.courseDetail.path[i][1]
                )
            )
        }
        Timber.tag(ContentValues.TAG).d("touchList 값 : $touchList")
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

                setDepartureLatLng()
                setTouchList()
            }
        }
    }

    private fun showReportBottomSheet() {
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