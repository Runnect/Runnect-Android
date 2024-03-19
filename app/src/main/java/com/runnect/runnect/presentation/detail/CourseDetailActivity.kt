package com.runnect.runnect.presentation.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import coil.load
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.databinding.ActivityCourseDetailBinding
import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.domain.entity.EditableCourseDetail
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen.COURSE_DISCOVER
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen.COURSE_DISCOVER_SEARCH
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen.COURSE_STORAGE_SCRAP
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen.MY_PAGE_UPLOAD_COURSE
import com.runnect.runnect.presentation.discover.DiscoverFragment.Companion.EXTRA_EDITABLE_DISCOVER_COURSE
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.presentation.discover.search.DiscoverSearchActivity
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.presentation.profile.ProfileActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_SHARE
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_USER_PROFILE
import com.runnect.runnect.util.analytics.EventName.VIEW_COURSE_DETAIL
import com.runnect.runnect.util.custom.dialog.CommonDialogFragment
import com.runnect.runnect.util.custom.dialog.CommonDialogText
import com.runnect.runnect.util.custom.dialog.RequireLoginDialogFragment
import com.runnect.runnect.util.custom.popup.PopupItem
import com.runnect.runnect.util.custom.popup.RunnectPopupMenu
import com.runnect.runnect.util.custom.toast.RunnectToast
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.applyScreenExitAnimation
import com.runnect.runnect.util.extension.getCompatibleSerializableExtra
import com.runnect.runnect.util.extension.getStampResId
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.extension.showWebBrowser
import com.runnect.runnect.util.mode.ScreenMode.EditMode
import com.runnect.runnect.util.mode.ScreenMode.ReadOnlyMode
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CourseDetailActivity :
    BindingActivity<ActivityCourseDetailBinding>(R.layout.activity_course_detail) {
    private val viewModel: CourseDetailViewModel by viewModels()
    private val isVisitorMode: Boolean = MainActivity.isVisitorMode

    // 플래그 변수
    private var isFromDeepLink: Boolean = false

    // 인텐트 부가 데이터
    private lateinit var rootScreen: CourseDetailRootScreen
    private var publicCourseId: Int = -1

    // 서버통신으로 초기화 할 데이터
    private lateinit var courseDetail: CourseDetail
    private lateinit var departureLatLng: LatLng
    private lateinit var connectedSpots: ArrayList<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        Analytics.logClickedItemEvent(VIEW_COURSE_DETAIL)

        updatePublicCourseIdFromDynamicLink { dynamicLinkHandled ->
            if (!dynamicLinkHandled) {
                initIntentExtraData()
            }
            addListener()
            addObserver()
            registerBackPressedCallback()
            getCourseDetail()
        }
    }

    private fun initIntentExtraData() {
        intent.getCompatibleSerializableExtra<CourseDetailRootScreen>(EXTRA_ROOT_SCREEN)?.let {
            rootScreen = it
        }
        publicCourseId = intent.getIntExtra(EXTRA_PUBLIC_COURSE_ID, -1)
        Timber.tag("intent-publicCourseId").d("$publicCourseId")
    }

    private fun updatePublicCourseIdFromDynamicLink(completion: (Boolean) -> Unit) {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                val deepLink: Uri? = pendingDynamicLinkData?.link
                if (deepLink != null) {
                    isFromDeepLink = true
                    publicCourseId = deepLink.getQueryParameter("courseId")?.toInt() ?: -1
                    if (publicCourseId != -1) {
                        Timber.tag("deeplink-publicCourseId").d("$publicCourseId")
                        completion(true)
                        return@addOnSuccessListener
                    }
                }
                completion(false)
            }
            .addOnFailureListener(this) { e ->
                Timber.e("getDynamicLink:onFailure", e)
                completion(false)
            }
    }

    private fun getCourseDetail() {
        viewModel.getCourseDetail(publicCourseId)
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackButtonByCurrentScreenMode()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun navigateToMainScreenWithBundle() {
        Intent(this@CourseDetailActivity, MainActivity::class.java).apply {
            putExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION, EXTRA_FROM_COURSE_DETAIL)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(this)
        }
        applyScreenExitAnimation()
    }

    private fun navigateToUserProfileWithBundle() {
        Intent(this@CourseDetailActivity, ProfileActivity::class.java).apply {
            putExtra(EXTRA_COURSE_USER_ID, courseDetail.userId)
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(this)
        }
        applyScreenEnterAnimation()
        Analytics.logClickedItemEvent(EVENT_CLICK_USER_PROFILE)
    }

    private fun handleBackButtonByCurrentScreenMode() {
        when (viewModel.currentScreenMode) {
            is ReadOnlyMode -> navigateToPreviousScreen()
            is EditMode -> showStopEditingDialog()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { newIntent ->
            newIntent.getCompatibleSerializableExtra<CourseDetailRootScreen>(EXTRA_ROOT_SCREEN)
                ?.let { rootScreen = it }
            publicCourseId = newIntent.getIntExtra(EXTRA_PUBLIC_COURSE_ID, -1)
            getCourseDetail()
        }
    }

    private fun navigateToPreviousScreen() {
        if (isFromDeepLink) {
            navigateToMainScreenWithBundle()
            isFromDeepLink = false
            return
        }

        when (rootScreen) {
            COURSE_STORAGE_SCRAP -> MainActivity.updateStorageScrapScreen()
            COURSE_DISCOVER -> setActivityResult<MainActivity>()
            COURSE_DISCOVER_SEARCH -> setActivityResult<DiscoverSearchActivity>()
            MY_PAGE_UPLOAD_COURSE -> setActivityResult<MyUploadActivity>()
        }

        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private inline fun <reified T : Activity> setActivityResult() {
        val updatedCourse = EditableDiscoverCourse(
            title = viewModel.title,
            scrap = binding.ivCourseDetailScrap.isSelected,
            isDeleted = viewModel.courseDeleteState.value is UiStateV2.Success
        )

        Intent(this@CourseDetailActivity, T::class.java).apply {
            putExtra(EXTRA_EDITABLE_DISCOVER_COURSE, updatedCourse)
            setResult(RESULT_OK, this)
        }
    }

    private fun addListener() {
        initBackButtonClickListener()

        initScrapButtonClickListener()
        initStartRunButtonClickListener()
        initEditFinishButtonClickListener()
        initUserInfoClickListener()

        initShareButtonClickListener()
        initShowMoreButtonClickListener()
    }

    private fun initBackButtonClickListener() {
        binding.ivCourseDetailBack.setOnClickListener {
            handleBackButtonByCurrentScreenMode()
        }
    }

    private fun initShowMoreButtonClickListener() {
        binding.btnShowMore.setOnClickListener { view ->
            if (courseDetail.isNowUser) {
                showEditDeletePopupMenu(view)
            } else {
                showReportPopupMenu(view)
            }
        }
    }

    private fun initEditFinishButtonClickListener() {
        binding.tvCourseDetailEditFinish.setOnClickListener {
            viewModel.patchPublicCourse(publicCourseId)
        }
    }

    private fun sendFirebaseDynamicLink(title: String, desc: String, image: String) {
        val link = "https://rnnt.page.link/?courseId=$publicCourseId"

        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix("https://rnnt.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            .setIosParameters(DynamicLink.IosParameters.Builder("com.runnect.Runnect-iOS").build())
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(title)
                    .setDescription(desc)
                    .setImageUrl(Uri.parse(image))
                    .build()
            )
            .buildShortDynamicLink()
            .addOnSuccessListener { result ->
                val shortLink = result.shortLink
                shareLink(shortLink.toString())
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun shareLink(url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        startActivity(Intent.createChooser(intent, "Share Link"))
    }

    private fun initShareButtonClickListener() {
        binding.btnShare.setOnClickListener {
            sendFirebaseDynamicLink(
                title = courseDetail.title,
                desc = courseDetail.description,
                image = courseDetail.image
            )
            Analytics.logClickedItemEvent(EVENT_CLICK_SHARE)
        }
    }

    private fun initUserInfoClickListener() {
        binding.constCourseDetailUserInfo.setOnClickListener {
            navigateToUserProfileWithBundle()
        }
    }

    private fun initStartRunButtonClickListener() {
        binding.btnCourseDetailStartRun.setOnClickListener {
            if (isVisitorMode) {
                showRequireLoginDialog()
                return@setOnClickListener
            }

            if (!::departureLatLng.isInitialized || connectedSpots.isEmpty()) {
                showSnackbar(binding.root, getString(R.string.course_detail_list_empty_error_msg))
                return@setOnClickListener
            }

            navigateToCountDownActivity()
        }
    }

    private fun navigateToCountDownActivity() {
        Intent(
            this@CourseDetailActivity,
            CountDownActivity::class.java
        ).apply {
            putExtra(
                EXTRA_COURSE_DATA, CourseData(
                    courseId = courseDetail.courseId,
                    publicCourseId = courseDetail.id,
                    touchList = connectedSpots,
                    startLatLng = departureLatLng,
                    departure = courseDetail.departure,
                    distance = courseDetail.distance.toFloat(),
                    image = courseDetail.image,
                    dataFrom = "detail"
                )
            )
            startActivity(this)
        }
    }

    private fun initScrapButtonClickListener() {
        binding.ivCourseDetailScrap.setOnClickListener {
            if (isVisitorMode) {
                RunnectToast.createToast(
                    this,
                    getString(R.string.visitor_mode_course_detail_scrap_warning_msg)
                ).show()
                return@setOnClickListener
            }

            viewModel.postCourseScrap(publicCourseId, !it.isSelected)
        }
    }


    private fun showStopEditingDialog() {
        val dialog = CommonDialogFragment.newInstance(
            CommonDialogText(
                getString(R.string.dialog_my_upload_course_detail_stop_editing_desc),
                getString(R.string.dialog_course_detail_stop_editing_no),
                getString(R.string.dialog_course_detail_stop_editing_yes)
            ),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = {
                // 편집 모드 -> 뒤로가기 버튼 -> 편집 중단 확인 -> 뷰에 원래 제목으로 보여줌.
                viewModel.restoreOriginalCourseDetail()
                enterReadMode()
            }
        )
        dialog.show(supportFragmentManager, TAG_MY_UPLOAD_COURSE_EDIT_DIALOG)
    }

    private fun showEditDeletePopupMenu(anchorView: View) {
        val popupItems = listOf(
            PopupItem(R.drawable.ic_detail_more_edit, getString(R.string.popup_menu_item_edit)),
            PopupItem(R.drawable.ic_detail_more_delete, getString(R.string.popup_menu_item_delete))
        )

        RunnectPopupMenu(anchorView.context, popupItems) { _, _, pos ->
            when (pos) {
                0 -> enterEditMode()
                1 -> showCourseDeleteDialog()
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showCourseDeleteDialog() {
        val dialog = CommonDialogFragment.newInstance(
            CommonDialogText(
                getString(R.string.dialog_my_upload_course_detail_delete_desc),
                getString(R.string.dialog_course_detail_delete_no),
                getString(R.string.dialog_course_detail_delete_yes)
            ),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = { viewModel.deleteUploadCourse(courseDetail.id) }
        )
        dialog.show(supportFragmentManager, TAG_MY_UPLOAD_COURSE_DELETE_DIALOG)
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

    private fun showRequireLoginDialog() {
        RequireLoginDialogFragment().show(supportFragmentManager, TAG_REQUIRE_LOGIN_DIALOG)
    }

    private fun enterEditMode() {
        viewModel.apply {
            updateCurrentScreenMode(EditMode)
            saveCurrentCourseDetail()
        }
        updateLayoutForEditMode()
    }

    private fun updateLayoutForEditMode() {
        with(binding) {
            groupCourseDetailReadMode.isVisible = false
            groupCourseDetailEditMode.isVisible = true
            btnShowMore.isVisible = false
        }
    }

    private fun enterReadMode() {
        viewModel.updateCurrentScreenMode(ReadOnlyMode)
        updateLayoutForReadMode()
    }

    private fun updateLayoutForReadMode() {
        with(binding) {
            groupCourseDetailEditMode.isVisible = false
            binding.groupCourseDetailReadMode.isVisible = true
            btnShowMore.isVisible = true
        }
    }

    private fun addObserver() {
        setupFromDeepLinkObserver()
        setupCourseGetStateObserver()
        setupCoursePatchStateObserver()
        setupCourseDeleteStateObserver()
        setupCourseScrapStateObserver()
    }

    private fun setupFromDeepLinkObserver() {
        viewModel.isDeepLinkLogin.observe(this) {
            // 딥링크로 진입했는데 로그인이 안 되어있는 경우
            if (viewModel.isDeepLinkLogin.value == false) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                viewModel.isDeepLinkLogin.value = true
            }
        }
    }

    private fun setupCourseGetStateObserver() {
        viewModel.courseGetState.observe(this) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    courseDetail = state.data ?: return@observe
                    binding.courseDetail = courseDetail

                    val editableCourseDetail = EditableCourseDetail(
                        title = courseDetail.title,
                        description = courseDetail.description
                    )
                    viewModel.updateCourseDetailEditText(editableCourseDetail)

                    updateUserProfileStamp()
                    updateUserLevel()
                    updateScrapState()

                    initDepartureLatLng()
                    initConnectedSpots()
                }

                is UiStateV2.Failure -> {
                    showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun initDepartureLatLng() {
        if (courseDetail.path.isNotEmpty()) {
            departureLatLng = LatLng(courseDetail.path[0][0], courseDetail.path[0][1])
        }
    }

    private fun initConnectedSpots() {
        connectedSpots = arrayListOf()
        for (spot in courseDetail.path) {
            connectedSpots.add(LatLng(spot[0], spot[1]))
        }
    }

    private fun setupCoursePatchStateObserver() {
        viewModel.coursePatchState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> binding.indeterminateBar.isVisible = true

                is UiStateV2.Success -> {
                    binding.indeterminateBar.isVisible = false

                    state.data?.let { response ->
                        viewModel.updateCourseDetailEditText(response)
                        updateCourseDetailTextView(response)
                    }

                    enterReadMode()
                    showToast(getString(R.string.course_detail_title_edit_success_msg))
                }

                is UiStateV2.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun updateCourseDetailTextView(courseDetail: EditableCourseDetail) {
        binding.apply {
            tvCourseDetailTitle.text = courseDetail.title
            tvCourseDetailDesc.text = courseDetail.description
        }
    }

    private fun setupCourseDeleteStateObserver() {
        viewModel.courseDeleteState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> binding.indeterminateBar.isVisible = true

                is UiStateV2.Success -> {
                    binding.indeterminateBar.isVisible = false
                    navigateToPreviousScreen()
                }

                is UiStateV2.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun setupCourseScrapStateObserver() {
        viewModel.courseScrapState.observe(this) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val response = state.data ?: return@observe
                    binding.tvCourseDetailScrapCount.text = response.scrapCount.toString()
                    binding.ivCourseDetailScrap.isSelected = response.scrapTF
                }

                is UiStateV2.Failure -> {
                    showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun updateUserProfileStamp() {
        val stampResId = getStampResId(
            stampId = courseDetail.stampId,
            resNameParam = RES_NAME,
            resType = RES_STAMP_TYPE,
            packageName = packageName
        )
        binding.ivCourseDetailProfileStamp.load(stampResId)
    }

    private fun updateUserLevel() {
        if (courseDetail.level == getString(R.string.course_detail_user_level_unknown_error)) {
            binding.tvCourseDetailProfileLv.isVisible = false
            binding.tvCourseDetailProfileLvIndicator.isVisible = false
        } else {
            binding.tvCourseDetailProfileLv.text = courseDetail.level
        }
    }

    private fun updateScrapState() {
        binding.ivCourseDetailScrap.isSelected = courseDetail.scrap
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) {
                    val focusView = Rect()
                    view.getGlobalVisibleRect(focusView)

                    val touchedX = event.x.toInt()
                    val touchedY = event.y.toInt()

                    if (!focusView.contains(touchedX, touchedY)) {
                        hideKeyboard(view)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    companion object {
        private const val REPORT_URL =
            "https://docs.google.com/forms/d/e/1FAIpQLSek2rkClKfGaz1zwTEHX3Oojbq_pbF3ifPYMYezBU0_pe-_Tg/viewform"
        private const val RES_NAME = "mypage_img_stamp_"
        private const val RES_STAMP_TYPE = "drawable"

        private const val EXTRA_ROOT_SCREEN = "rootScreen"
        private const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        private const val EXTRA_COURSE_DATA = "CourseData"
        private const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        private const val EXTRA_FROM_COURSE_DETAIL = "fromCourseDetail"
        private const val EXTRA_COURSE_USER_ID = "courseUserId"

        private const val TAG_MY_UPLOAD_COURSE_DELETE_DIALOG = "MY_UPLOAD_COURSE_DELETE_DIALOG"
        private const val TAG_MY_UPLOAD_COURSE_EDIT_DIALOG = "MY_UPLOAD_COURSE_EDIT_DIALOG"
        private const val TAG_REQUIRE_LOGIN_DIALOG = "REQUIRE_LOGIN_DIALOG"
    }
}