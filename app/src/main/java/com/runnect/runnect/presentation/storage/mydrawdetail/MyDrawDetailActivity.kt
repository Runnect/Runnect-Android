package com.runnect.runnect.presentation.storage.mydrawdetail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetail
import com.runnect.runnect.databinding.ActivityMyDrawDetailBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.custom.dialog.CommonDialogFragment
import com.runnect.runnect.util.custom.dialog.CommonDialogText
import com.runnect.runnect.util.custom.popup.PopupItem
import com.runnect.runnect.util.custom.popup.RunnectPopupMenu
import com.runnect.runnect.util.extension.PermissionUtil
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.extension.showWebBrowser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyDrawDetailActivity :
    BindingActivity<ActivityMyDrawDetailBinding>(R.layout.activity_my_draw_detail) {
    val viewModel: MyDrawDetailViewModel by viewModels()
    private val selectList = arrayListOf<Int>()
    private val touchList = arrayListOf<LatLng>()
    private lateinit var departureLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        getMyDrawDetail()
        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    private fun getMyDrawDetail() {
        val courseId = intent.getIntExtra(EXTRA_COURSE_ID, 0)
        if (courseId == 0) {
            showSnackbar(
                anchorView = binding.root,
                message = "fail get my draw course detail"
            )
            return
        }

        selectList.add(courseId)
        viewModel.getMyDrawDetail(courseId)
    }

    private fun addListener() {
        initBackButtonClickListener()
        initMoreButtonClickListener()
        initDrawButtonClickListener()
    }

    private fun initBackButtonClickListener() {
        binding.ivMyDrawBack.setOnClickListener {
            navigateToPreviousScreenWithAnimation()
        }
    }

    private fun initMoreButtonClickListener() {
        binding.ivMyDrawMore.setOnClickListener { view ->
            if (viewModel.isNowUser) {
                showEditDeletePopupMenu(view)
            } else {
                showReportPopupMenu(view)
            }
        }
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
        TODO("Not yet implemented")
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

    private fun initDrawButtonClickListener() {
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

    private fun showPermissionDeniedToast() {
        showToast(getString(R.string.location_permission_denied))
    }

    private fun navigateToCountDown() {
        startActivity(Intent(this, CountDownActivity::class.java).apply {
            putExtra(EXTRA_COURSE_DATA, viewModel.myDrawToRunData.value)
        })
    }

    fun addObserver() {
        setupCourseGetStateObserver()
        setupCourseDeleteStateObserver()
    }

    private fun setupCourseGetStateObserver() {
        viewModel.getResult.observe(this) { response ->
            setTitle(response)
            setImage(response)
            setDepartureLatLng(response)
            setTouchList(response)
            setPutExtraValue(response)
        }
    }

    private fun setTitle(src: ResponseGetMyDrawDetail) {
        binding.tvMyDrawTitle.text = src.data.course.title
    }

    private fun setImage(src: ResponseGetMyDrawDetail) {
        with(binding) {
            Glide.with(ivMyDrawDetail.context)
                .load(src.data.course.image.toUri())
                .centerCrop()
                .into(ivMyDrawDetail)
            tvCourseDistanceRecord.text = src.data.course.distance.toString()
        }
    }

    private fun setDepartureLatLng(src: ResponseGetMyDrawDetail) {
        departureLatLng =
            LatLng(src.data.course.path[0][0], src.data.course.path[0][1])
        Timber.tag(ContentValues.TAG).d("departureLatLng 값 : $departureLatLng")
    }

    private fun setTouchList(src: ResponseGetMyDrawDetail) {
        for (i in 1 until src.data.course.path.size) {
            touchList.add(LatLng(src.data.course.path[i][0], src.data.course.path[i][1]))
        }
    }

    private fun setPutExtraValue(src: ResponseGetMyDrawDetail) {
        viewModel.myDrawToRunData.value = CourseData(
            courseId = src.data.course.id,
            publicCourseId = null,
            touchList = touchList,
            startLatLng = departureLatLng,
            departure = src.data.course.departure.name,
            distance = src.data.course.distance,
            image = src.data.course.image,
            dataFrom = "fromMyDrawDetail"
        )
    }

    private fun setupCourseDeleteStateObserver() {
        viewModel.myDrawCourseDeleteState.observe(this) { state ->
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
        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        const val EXTRA_COURSE_DATA = "CourseData"
        const val EXTRA_COURSE_ID = "courseId"
    }
}