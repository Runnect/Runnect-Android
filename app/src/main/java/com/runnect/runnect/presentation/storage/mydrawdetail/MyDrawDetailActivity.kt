package com.runnect.runnect.presentation.storage.mydrawdetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.databinding.ActivityMyDrawDetailBinding
import com.runnect.runnect.databinding.LayoutCommonToolbarBinding
import com.runnect.runnect.domain.entity.MyDrawCourseDetail
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.custom.dialog.CommonDialogFragment
import com.runnect.runnect.util.custom.dialog.CommonDialogText
import com.runnect.runnect.util.custom.popup.PopupItem
import com.runnect.runnect.util.custom.popup.RunnectPopupMenu
import com.runnect.runnect.util.custom.toolbar.CommonToolbarLayout
import com.runnect.runnect.util.custom.toolbar.ToolbarMenu
import com.runnect.runnect.util.extension.PermissionUtil
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.extension.showWebBrowser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyDrawDetailActivity :
    BindingActivity<ActivityMyDrawDetailBinding>(R.layout.activity_my_draw_detail),
    CommonToolbarLayout {
    val viewModel: MyDrawDetailViewModel by viewModels()
    private lateinit var departureLatLng: LatLng
    private val touchList = arrayListOf<LatLng>()
    private val selectList = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        getMyDrawDetail()
        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    override val toolbarBinding: LayoutCommonToolbarBinding
        get() = binding.layoutCommonToolbar

    override fun initToolBarLayout() {
        addMenuTo(
            CommonToolbarLayout.LEFT,
            ToolbarMenu.Icon(
                resourceId = R.drawable.all_back_arrow,
                clickEvent = {
                    navigateToPreviousScreenWithAnimation()
                }
            ),
            ToolbarMenu.Text(
                titleText = viewModel.myDrawCourseDetail.value?.title,
                padding = 0,
                textSize = 15,
                fontRes = R.font.pretendard_medium
            )
        )
    }

    private fun getMyDrawDetail() {
        val courseId = intent.getIntExtra(EXTRA_COURSE_ID, 0)
        selectList.add(courseId)
        viewModel.getMyDrawDetail(courseId)
    }

    private fun addListener() {
        initBackButtonClickListener()
        initMoreButtonClickListener()
        initRunningButtonClickListener()
    }

    fun addObserver() {
        setupCourseGetStateObserver()
        setupCourseDeleteStateObserver()
    }

    private fun initBackButtonClickListener() {
//        binding.ivMyDrawBack.setOnClickListener {
//            navigateToPreviousScreenWithAnimation()
//        }
    }

    private fun initMoreButtonClickListener() {
//        binding.ivMyDrawMore.setOnClickListener { view ->
//            val isNowUser = viewModel.myDrawCourseDetail.value?.isNowUser
//            if (isNowUser == true) {
//                showEditDeletePopupMenu(view)
//            } else {
//                showReportPopupMenu(view)
//            }
//        }
    }

    private fun showEditDeletePopupMenu(anchorView: View) {
        val popupItems = listOf(
            PopupItem(R.drawable.ic_detail_more_edit, getString(R.string.popup_menu_item_edit)),
            PopupItem(R.drawable.ic_detail_more_delete, getString(R.string.popup_menu_item_delete))
        )

        RunnectPopupMenu(anchorView.context, popupItems) { _, _, pos ->
            when (pos) {
                0 -> showTitleEditBottomSheet()
                1 -> showCourseDeleteDialog()
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showTitleEditBottomSheet() {

    }

    private fun showCourseDeleteDialog() {
        val dialog = CommonDialogFragment.newInstance(
            CommonDialogText(
                getString(R.string.dialog_my_upload_course_detail_delete_desc),
                getString(R.string.dialog_course_detail_delete_no),
                getString(R.string.dialog_course_detail_delete_yes)
            ),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = { viewModel.deleteMyDrawCourse(selectList) }
        )
        dialog.show(supportFragmentManager, TAG_MY_DRAW_COURSE_DELETE_DIALOG)
    }

    private fun showReportPopupMenu(anchorView: View) {
        val popupItems = listOf(
            PopupItem(
                R.drawable.ic_detail_more_report,
                getString(R.string.popup_menu_item_report)
            )
        )

        RunnectPopupMenu(anchorView.context, popupItems) { _, _, _ ->
            showWebBrowser(REPORT_URL)
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun initRunningButtonClickListener() {
        binding.btnMyDrawDetailRun.setOnClickListener {
            this.let {
                PermissionUtil.requestLocationPermission(
                    context = it,
                    onPermissionGranted = { navigateToCountDown() },
                    onPermissionDenied = { showPermissionDeniedToast() },
                    permissionType = PermissionUtil.PermissionType.LOCATION
                )
            }
        }
    }

    private fun navigateToCountDown() {
        startActivity(Intent(this, CountDownActivity::class.java).apply {
            putExtra(EXTRA_COURSE_DATA, viewModel.extraDataForRunning.value)
        })
    }

    private fun showPermissionDeniedToast() {
        showToast(getString(R.string.location_permission_denied))
    }

    private fun setupCourseGetStateObserver() {
        viewModel.courseGetState.observe(this) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val course = state.data
                    initMyDrawCourseDetail(course)
                    initExtraDataForRunning(course)
                }

                is UiStateV2.Failure -> {
                    showSnackbar(
                        anchorView = binding.root,
                        message = state.msg
                    )
                }

                else -> {}
            }
        }
    }

    private fun initMyDrawCourseDetail(course: MyDrawCourseDetail) {
        setImage(course)
        setDistance(course)
        setDepartureLatLng(course)
        setTouchList(course)
    }

    private fun setImage(course: MyDrawCourseDetail) {
        with(binding) {
            Glide.with(ivMyDrawDetail.context)
                .load(course.imgUrl)
                .centerCrop()
                .into(ivMyDrawDetail)
        }
    }

    private fun setDistance(course: MyDrawCourseDetail) {
        binding.tvCourseDistanceRecord.text = course.distance.toString()
    }

    private fun setDepartureLatLng(course: MyDrawCourseDetail) {
        departureLatLng = LatLng(course.path[0][0], course.path[0][1])
    }

    private fun setTouchList(course: MyDrawCourseDetail) {
        for (i in 1 until course.path.size) {
            touchList.add(LatLng(course.path[i][0], course.path[i][1]))
        }
    }

    private fun initExtraDataForRunning(course: MyDrawCourseDetail) {
        viewModel.extraDataForRunning.value = CourseData(
            courseId = course.courseId,
            publicCourseId = null,
            touchList = touchList,
            startLatLng = departureLatLng,
            departure = course.departureName,
            distance = course.distance,
            image = course.imgUrl,
            dataFrom = EXTRA_MY_DRAW_COURSE_DETAIL
        )
    }

    private fun setupCourseDeleteStateObserver() {
        viewModel.courseDeleteState.observe(this) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    Intent(this@MyDrawDetailActivity, MainActivity::class.java).apply {
                        putExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION, "fromDeleteMyDrawDetail")
                    }.apply {
                        startActivity(this)
                    }
                    navigateToPreviousScreenWithAnimation()
                }

                is UiStateV2.Failure -> {
                    showSnackbar(
                        anchorView = binding.root,
                        message = ""
                    )
                }

                else -> {}
            }
        }
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToPreviousScreenWithAnimation()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {
        private const val REPORT_URL =
            "https://docs.google.com/forms/d/e/1FAIpQLSek2rkClKfGaz1zwTEHX3Oojbq_pbF3ifPYMYezBU0_pe-_Tg/viewform"
        private const val TAG_MY_DRAW_COURSE_DELETE_DIALOG = "MY_DRAW_COURSE_DELETE_DIALOG"
        private const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        private const val EXTRA_MY_DRAW_COURSE_DETAIL = "fromMyDrawDetail"
        private const val EXTRA_COURSE_DATA = "CourseData"
        private const val EXTRA_COURSE_ID = "courseId"
    }
}