package com.runnect.runnect.presentation.storage.mydrawdetail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.databinding.ActivityMyDrawDetailBinding
import com.runnect.runnect.databinding.BottomsheetRequireCourseNameBinding
import com.runnect.runnect.databinding.LayoutCommonToolbarBinding
import com.runnect.runnect.domain.entity.MyDrawCourseDetail
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.scheme.SchemeActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.presentation.storage.StorageMyDrawFragment
import com.runnect.runnect.util.custom.dialog.CommonDialogFragment
import com.runnect.runnect.util.custom.dialog.CommonDialogText
import com.runnect.runnect.util.custom.popup.PopupItem
import com.runnect.runnect.util.custom.toolbar.CommonToolbarLayout
import com.runnect.runnect.util.custom.toolbar.ToolbarMenu
import com.runnect.runnect.util.dynamiclink.RunnectDynamicLink
import com.runnect.runnect.util.extension.PermissionUtil
import com.runnect.runnect.util.extension.applyScreenExitAnimation
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.extension.showWebBrowser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyDrawDetailActivity :
    BindingActivity<ActivityMyDrawDetailBinding>(R.layout.activity_my_draw_detail),
    CommonToolbarLayout {
    val viewModel: MyDrawDetailViewModel by viewModels()
    private lateinit var myDrawCourseDetail: MyDrawCourseDetail
    private lateinit var departureLatLng: LatLng
    private val touchList = arrayListOf<LatLng>()
    private val selectList = arrayListOf<Int>()
    private var courseId = -1
    private var isFromDynamicLink = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        initCourseIdExtra()
        getMyDrawDetail()
        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    private fun initCourseIdExtra() {
        val idFromLink = intent.getIntExtra(SchemeActivity.EXTRA_FROM_DYNAMIC_LINK, -1)
        if (idFromLink != -1) {
            isFromDynamicLink = true
            courseId = idFromLink
            return
        }

        val idFromRootScreen = intent.getIntExtra(StorageMyDrawFragment.EXTRA_COURSE_ID, -1)
        if (idFromRootScreen != -1) {
            courseId = idFromRootScreen
            return
        }
    }

    private fun getMyDrawDetail() {
        selectList.add(courseId)
        viewModel.getMyDrawDetail(courseId)
    }

    private fun addListener() {
        initRunningButtonClickListener()
    }

    fun addObserver() {
        setupCourseGetStateObserver()
        setupCourseDeleteStateObserver()
        setupCoursePatchStateObserver()
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
                is UiStateV2.Loading -> {
                    showLoadingProgressBar()
                }

                is UiStateV2.Success -> {
                    myDrawCourseDetail = state.data
                    initToolBarLayout()
                    initMyDrawCourseDetail(myDrawCourseDetail)
                    initExtraDataForRunning(myDrawCourseDetail)
                    dismissLoadingProgressBar()
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                    showSnackbar(
                        anchorView = binding.root,
                        message = state.msg
                    )
                }

                else -> {}
            }
        }
    }

    private fun showLoadingProgressBar() {
        with(binding) {
            pbMyDrawDetail.isVisible = true
            ivMyDrawDetail.isVisible = false
        }
    }

    private fun dismissLoadingProgressBar() {
        with(binding) {
            pbMyDrawDetail.isVisible = false
            ivMyDrawDetail.isVisible = true
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
            dataFrom = EXTRA_FROM_MY_DRAW_DETAIL
        )
    }

    private fun setupCourseDeleteStateObserver() {
        viewModel.courseDeleteState.observe(this) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    Intent(this@MyDrawDetailActivity, MainActivity::class.java).apply {
                        putExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION, EXTRA_DELETE_MY_DRAW_COURSE)
                    }.apply {
                        startActivity(this)
                    }
                    navigateToPreviousScreenWithAnimation()
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

    private fun setupCoursePatchStateObserver() {
        viewModel.coursePatchState.observe(this) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val textMenu = toolbarBinding.llLeftMenu[1] as? TextView
                    textMenu?.text = state.data.title
                    showToast(getString(R.string.my_draw_detail_update_title_success_msg))
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

    override val toolbarBinding: LayoutCommonToolbarBinding
        get() = binding.layoutCommonToolbar

    override fun initToolBarLayout() {
        addLeftMenu()
        addRightMenu()
    }

    private fun addLeftMenu() {
        addMenuTo(
            CommonToolbarLayout.LEFT,
            ToolbarMenu.Icon(
                resourceId = R.drawable.all_back_arrow,
                clickEvent = {
                    initBackButtonClickListener()
                }
            ),
            ToolbarMenu.Text(
                titleText = myDrawCourseDetail.title,
                padding = 0,
                textSize = 18,
                fontRes = R.font.pretendard_bold
            )
        )
    }

    private fun initBackButtonClickListener() {
        if (isFromDynamicLink) {
            navigateToMainScreen()
            return
        }
        setActivityResult<MainActivity>()
        navigateToPreviousScreenWithAnimation()
    }

    private fun addRightMenu() {
        if (isFromDynamicLink) {
            if (myDrawCourseDetail.isNowUser) {
                addShareEditDeleteMenu()
            }
            return
        }

        addShareEditDeleteMenu()
    }

    private fun addShareEditDeleteMenu() {
        addMenuTo(
            CommonToolbarLayout.RIGHT,
            ToolbarMenu.Icon(
                resourceId = R.drawable.ic_share,
                clickEvent = {
                    initShareButtonClickListener()
                }
            ),
            ToolbarMenu.Popup(
                resourceId = R.drawable.showmorebtn,
                popupItems = listOf(
                    PopupItem(
                        R.drawable.ic_detail_more_edit,
                        getString(R.string.popup_menu_item_edit)
                    ),
                    PopupItem(
                        R.drawable.ic_detail_more_delete,
                        getString(R.string.popup_menu_item_delete)
                    )
                ),
                menuItemClickListener = { _, _, pos ->
                    when (pos) {
                        0 -> createTitleEditBottomSheet().show()
                        1 -> showCourseDeleteDialog()
                    }
                }
            )
        )
    }

    private fun initShareButtonClickListener() {
        createDynamicLink(
            title = viewModel.courseTitle,
            imgUrl = myDrawCourseDetail.imgUrl
        )
    }

    private fun createDynamicLink(title: String, imgUrl: String) {
        val link = "${RunnectDynamicLink.BASE_URL}/?${RunnectDynamicLink.KEY_PRIVATE_COURSE_ID}=${courseId}"
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix(RunnectDynamicLink.BASE_URL)
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            .setIosParameters(
                DynamicLink.IosParameters.Builder(RunnectDynamicLink.IOS_BUNDLE_ID).build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(title)
                    .setImageUrl(Uri.parse(imgUrl))
                    .build()
            )
            .buildShortDynamicLink()
            .addOnSuccessListener { result ->
                Timber.d("shortLink: ${result.shortLink}")
                shareDynamicLink(result.shortLink.toString())
            }
            .addOnFailureListener { t ->
                Timber.e("sendDynamicLink fail: ${t.message}")
            }
    }

    private fun shareDynamicLink(shortLink: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = RunnectDynamicLink.SEND_INTENT_MIME_TYPE
            putExtra(Intent.EXTRA_TEXT, shortLink)
        }
        startActivity(Intent.createChooser(intent, RunnectDynamicLink.INTENT_CHOOSER_TITLE))
    }

    private fun createTitleEditBottomSheet(): BottomSheetDialog {
        val bottomSheetBinding = BottomsheetRequireCourseNameBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root
        val etCourseTitle = bottomSheetBinding.etCourseName
        val btnComplete = bottomSheetBinding.btnCreateCourse

        etCourseTitle.setText(myDrawCourseDetail.title)
        etCourseTitle.addTextChangedListener { title ->
            val isValidTitle = !title.isNullOrBlank()
            with(btnComplete) {
                setBackgroundResource(if (isValidTitle) R.drawable.radius_10_m1_button else R.drawable.radius_10_g3_button)
                isEnabled = isValidTitle
            }
            viewModel.updateCourseTitle(if (isValidTitle) title.toString() else "")
        }

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        btnComplete.setOnClickListener {
            viewModel.patchCourseTitle(courseId)
            hideKeyboard(etCourseTitle)
            bottomSheetDialog.dismiss()
        }

        return bottomSheetDialog
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

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                initBackButtonClickListener()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun navigateToMainScreen() {
        Intent(this, MainActivity::class.java).apply {
            putExtra(
                EXTRA_FRAGMENT_REPLACEMENT_DIRECTION,
                EXTRA_FROM_MY_DRAW_DETAIL
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(this)
        }
        applyScreenExitAnimation()
    }

    private inline fun <reified T : Activity> setActivityResult() {
        Intent(this, T::class.java).apply {
            putExtra(StorageMyDrawFragment.EXTRA_COURSE_TITLE, viewModel.courseTitle)
            setResult(RESULT_OK, this)
        }
    }

    companion object {
        private const val REPORT_URL =
            "https://docs.google.com/forms/d/e/1FAIpQLSek2rkClKfGaz1zwTEHX3Oojbq_pbF3ifPYMYezBU0_pe-_Tg/viewform"
        private const val TAG_MY_DRAW_COURSE_DELETE_DIALOG = "MY_DRAW_COURSE_DELETE_DIALOG"
        private const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        private const val EXTRA_FROM_MY_DRAW_DETAIL = "fromMyDrawDetail"
        private const val EXTRA_DELETE_MY_DRAW_COURSE = "fromDeleteMyDrawDetail"
        private const val EXTRA_COURSE_DATA = "CourseData"
    }
}