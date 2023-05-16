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
import androidx.core.view.isVisible
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
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.presentation.report.ReportActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_edit_mode.*
import kotlinx.android.synthetic.main.custom_dialog_require_login.view.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import timber.log.Timber

@AndroidEntryPoint
class CourseDetailActivity :
    BindingActivity<ActivityCourseDetailBinding>(R.layout.activity_course_detail) {
    private val viewModel: CourseDetailViewModel by viewModels()
    private var courseId: Int = 0
    private var root: String = ""


    lateinit var departureLatLng: LatLng
    private val touchList = arrayListOf<LatLng>()
    private lateinit var deleteDialog: AlertDialog
    private lateinit var editBottomSheet: BottomSheetDialog
    private lateinit var editInterruptDialog: AlertDialog

    var isVisitorMode: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkVisitorMode()

        courseId = intent.getIntExtra("courseId", 0)
        root = intent.getStringExtra("root").toString()
        addListener()
        initView()
        getCourseDetail()
        addObserver()
        initEditBottomSheet()
        initDeleteDialog()
        setDeleteDialogClickEvent()
        initEditInterruptedDialog()
        setEditInterruptedDialog()
    }


    private fun checkVisitorMode() {
        isVisitorMode =
            PreferenceManager.getString(ApplicationClass.appContext, "access")!! == "visitor"
    }


    private fun initView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun addListener() {
        binding.ivCourseDetailBack.setOnClickListener {
            if (viewModel.editMode.value == true) {
                editInterruptDialog.show()
            } else {
                if (!viewModel.isEdited) {
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                } else {
                    if (root == MY_UPLOAD_ACTIVITY_TAG) {
                        val intent = Intent(this, MyUploadActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                }
            }
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
        binding.btnShowMore.setOnClickListener {
            if (root == MY_UPLOAD_ACTIVITY_TAG) {
                editBottomSheet.show()
            } else {
                bottomSheet()
            }
        }
        binding.tvCourseDetailEditFinish.setOnClickListener {
            //수정 API 호출
            viewModel.patchUpdatePublicCourse(courseId)
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

    private fun enterEditMode() {
        viewModel.convertMode()
        viewModel.titleForInterruption.value = viewModel.editTitle.value.toString()
        viewModel.contentForInterruption.value = viewModel.editContent.value.toString()
        updateLayoutForEditMode()
    }

    private fun updateLayoutForEditMode() {
        with(binding) {
            groupCourseDetailReadMode.isVisible = false
            groupCourseDetailEditMode.isVisible = true
            btnShowMore.isVisible = false
        }
        editBottomSheet.dismiss()
    }

    private fun enterReadMode() {
        viewModel.convertMode()
        updateLayoutForReadMode()
    }

    private fun updateLayoutForReadMode() {
        with(binding) {
            groupCourseDetailEditMode.isVisible = false
            binding.groupCourseDetailReadMode.isVisible = true
            btnShowMore.isVisible = true
        }
    }

    private fun getCourseDetail() {
        viewModel.getCourseDetail(courseId)
    }

    private fun setDepartureLatLng() {
        departureLatLng =
            LatLng(viewModel.courseDetail.path[0][0], viewModel.courseDetail.path[0][1])
    }

    private fun setTouchList() {
        for (i in 1 until viewModel.courseDetail.path.size) {
            touchList.add(
                LatLng(
                    viewModel.courseDetail.path[i][0],
                    viewModel.courseDetail.path[i][1]
                )
            )
        }
    }

    private fun addObserver() {
        viewModel.courseDetailState.observe(this) { state ->
            if (state == UiState.Success) {
                with(binding) {
                    with(viewModel.courseDetail) {
                        val stampResId = this@CourseDetailActivity.getStampResId(
                            viewModel.stampId.value,
                            RES_NAME, RES_STAMP_TYPE, packageName
                        )
                        ivCourseDetailProfileStamp.load(stampResId)
                        ivCourseDetailProfileNickname.text = nickname
                        tvCourseDetailProfileLv.text = level
                        ivCourseDetailScrap.isSelected = scrap
                    }

                }

                setDepartureLatLng()
                setTouchList()
            }
        }

        viewModel.myUploadDeleteState.observe(this) { state ->
            when (state) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    if (root == MY_UPLOAD_ACTIVITY_TAG) {
                        val intent = Intent(this, MyUploadActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
                }
                else -> {}
            }
        }
        viewModel.editMediator.observe(this) {}
        viewModel.isEditFinishEnable.observe(this) {
            with(binding.tvCourseDetailEditFinish) {
                isActivated = it
                isClickable = it
            }
        }
        viewModel.courseUpdateState.observe(this) { state ->
            when (state) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    handleSuccessfulCourseUpdate()
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
                }
                else -> {}
            }
        }
    }

    private fun handleSuccessfulCourseUpdate() {
        binding.indeterminateBar.isVisible = false
        viewModel.editTitle.value = viewModel.titleForInterruption.value
        viewModel.editContent.value = viewModel.contentForInterruption.value
        viewModel.isEdited = true
        enterReadMode()
        showToast("수정이 완료되었습니다")
    }

    private fun initEditBottomSheet() {
        editBottomSheet = setEditBottomSheet()
        setEditBottomSheetClickEvent()
    }

    private fun setEditBottomSheetClickEvent() {
        editBottomSheet.setEditBottomSheetClickListener { which ->
            when (which) {
                editBottomSheet.layout_edit_frame -> {
                    enterEditMode()
                }
                editBottomSheet.layout_delete_frame -> {
                    deleteDialog.show()
                }
            }
        }
    }

    private fun initDeleteDialog() {
        deleteDialog =
            setCustomDialog(
                layoutInflater, binding.root,
                DELETE_DIALOG_DESC,
                DELETE_DIALOG_YES_BTN
            )
    }

    private fun setDeleteDialogClickEvent() {
        deleteDialog.setDialogClickListener { which ->
            when (which) {
                deleteDialog.btn_delete_yes -> {
                    viewModel.deleteUploadCourse(courseId)
                }
            }

        }
    }

    private fun initEditInterruptedDialog() {
        editInterruptDialog = setCustomDialog(
            layoutInflater,
            binding.root,
            EDIT_INTERRUPT_DIALOG_DESC,
            EDIT_INTERRUPT_DIALOG_YES_BTN,
            EDIT_INTERRUPT_DIALOG_NO_BTN
        )
    }

    private fun setEditInterruptedDialog() {
        editInterruptDialog.setDialogClickListener { which ->
            when (which) {
                editInterruptDialog.btn_delete_yes -> {
                    enterReadMode()
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

    companion object {
        const val DELETE_DIALOG_DESC = "코스를 정말로 삭제하시겠어요?"
        const val DELETE_DIALOG_YES_BTN = "삭제하기"
        const val EDIT_INTERRUPT_DIALOG_DESC = "     게시글 수정을 종료할까요?\n종료 시 수정 내용이 반영되지 않아요."
        const val EDIT_INTERRUPT_DIALOG_YES_BTN = "예"
        const val EDIT_INTERRUPT_DIALOG_NO_BTN = "아니오"
        const val MY_UPLOAD_ACTIVITY_TAG = "upload"
        const val RES_NAME = "mypage_img_stamp_"
        const val RES_STAMP_TYPE = "drawable"
    }
}