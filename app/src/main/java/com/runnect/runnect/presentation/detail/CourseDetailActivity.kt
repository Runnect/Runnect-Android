package com.runnect.runnect.presentation.detail

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import coil.load
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.databinding.ActivityCourseDetailBinding
import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.domain.entity.EditableCourseDetail
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen.*
import com.runnect.runnect.presentation.discover.DiscoverFragment.Companion.KEY_EDITABLE_DISCOVER_COURSE
import com.runnect.runnect.presentation.discover.search.DiscoverSearchActivity
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.custom.dialog.CommonDialogFragment
import com.runnect.runnect.util.custom.dialog.CommonDialogText
import com.runnect.runnect.util.custom.popup.PopupItem
import com.runnect.runnect.util.custom.dialog.RequireLoginDialogFragment
import com.runnect.runnect.util.custom.popup.RunnectPopupMenu
import com.runnect.runnect.util.custom.toast.RunnectToast
import com.runnect.runnect.util.extension.applyScreenExitAnimation
import com.runnect.runnect.util.extension.getCompatibleSerializableExtra
import com.runnect.runnect.util.extension.getStampResId
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showToast
import com.runnect.runnect.util.extension.showWebBrowser
import com.runnect.runnect.util.mode.ScreenMode.*
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

        initIntentExtraData()
        updatePublicCourseIdFromDeepLink()
        getCourseDetail()

        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    private fun initIntentExtraData() {
        intent.getCompatibleSerializableExtra<CourseDetailRootScreen>(EXTRA_ROOT_SCREEN)?.let {
            rootScreen = it
        }
        publicCourseId = intent.getIntExtra(EXTRA_PUBLIC_COURSE_ID, 0)
    }

    private fun updatePublicCourseIdFromDeepLink() {
        // 딥링크를 통해 열린 경우
        if (Intent.ACTION_VIEW == intent.action) {
            isFromDeepLink = true
            val uri = intent.data
            if (uri != null) {
                // 여기서 androidExecutionParams 값들을 받아와 어떠한 상세 페이지를 띄울지 결정할 수 있음.
                publicCourseId = uri.getQueryParameter("publicCourseId")!!.toInt()
                Timber.tag("deeplink-publicCourseId").d("$publicCourseId")
            }
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

    private fun handleBackButtonByCurrentScreenMode() {
        when (viewModel.currentScreenMode) {
            is ReadOnlyMode -> navigateToPreviousScreen()
            is EditMode -> showStopEditingDialog()
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
            MY_PAGE_UPLOAD_COURSE -> navigateToMyUploadCourseScreen()
        }

        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private inline fun <reified E : Activity> setActivityResult() {
        val updatedCourse = EditableDiscoverCourse(
            title = viewModel.title,
            scrap = binding.ivCourseDetailScrap.isSelected
        )

        Intent(this@CourseDetailActivity, E::class.java).apply {
            putExtra(KEY_EDITABLE_DISCOVER_COURSE, updatedCourse)
            setResult(RESULT_OK, this)
        }
    }

    private fun navigateToMyUploadCourseScreen() {
        Intent(this, MyUploadActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun addListener() {
        initBackButtonClickListener()

        initScrapButtonClickListener()
        initStartRunButtonClickListener()
        initEditFinishButtonClickListener()

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

    private fun initShareButtonClickListener() {
        binding.btnShare.setOnClickListener {
            sendKakaoLink(
                title = courseDetail.title,
                desc = courseDetail.description,
                image = courseDetail.image
            )
        }
    }

    // todo: 함수를 더 작게 분리하는 게 좋을 거 같아요! @우남
    private fun sendKakaoLink(title: String, desc: String, image: String) {
        // 메시지 템플릿 만들기 (피드형)
        val defaultFeed = FeedTemplate(
            content = Content(
                title = title,
                description = desc,
                imageUrl = image,
                link = Link(
                    mobileWebUrl = "https://play.google.com/store/apps/details?id=com.runnect.runnect"
                )
            ),
            buttons = listOf(
                Button(
                    "자세히 보기",
                    Link(
                        //이 부분을 사용해서 어떤 상세페이지를 띄울지 결정할수 있다
                        androidExecutionParams = mapOf(
                            "publicCourseId" to publicCourseId.toString(),
                        )
                    ),
                )
            )
        )

        // 피드 메시지 보내기
        if (context?.let { LinkClient.instance.isKakaoLinkAvailable(it) } == true) {
            // 카카오톡으로 카카오링크 공유 가능
            context?.let {
                LinkClient.instance.defaultTemplate(it, defaultFeed) { linkResult, error ->
                    if (error != null) {
                        Timber.tag("kakao_link").d("카카오링크 보내기 실패: $error")
                    } else if (linkResult != null) {
                        Timber.tag("kakao_link").d("카카오링크 보내기 성공: ${linkResult.intent}")

                        startActivity(linkResult.intent) //카카오톡이 깔려있을 경우 카카오톡으로 넘기기

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않음
                        Timber.tag("kakao_link").d("Warning Msg: ${linkResult.warningMsg}")
                        Timber.tag("kakao_link").d("Argument Msg: ${linkResult.argumentMsg}")
                    }
                }
            }
        } else {  // 카카오톡 미설치: 웹 공유 사용 권장
            // 웹 공유 예시 코드
            val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultFeed)

            // 1. CustomTabs으로 Chrome 브라우저 열기
            try {
                context?.let { KakaoCustomTabsClient.openWithDefault(it, sharerUrl) }
            } catch (e: UnsupportedOperationException) {
                // Chrome 브라우저가 없을 때
                Toast.makeText(context, "chrome 또는 인터넷 브라우저를 설치해주세요", Toast.LENGTH_SHORT).show()
            }

            // 2. CustomTabs으로 디바이스 기본 브라우저 열기
            try {
                context?.let { KakaoCustomTabsClient.open(it, sharerUrl) }
            } catch (e: ActivityNotFoundException) {
                // 인터넷 브라우저가 없을 때
                Toast.makeText(context, "chrome 또는 인터넷 브라우저를 설치해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initStartRunButtonClickListener() {
        binding.btnCourseDetailStartRun.setOnClickListener {
            if (isVisitorMode) {
                showRequireLoginDialog()
                return@setOnClickListener
            }

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

            it.isSelected = !it.isSelected
            viewModel.postCourseScrap(publicCourseId, it.isSelected)
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
                viewModel.restoreOriginalContents()
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

    private fun RunnectPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, POPUP_MENU_X_OFFSET, POPUP_MENU_Y_OFFSET, Gravity.END)
    }

    private fun showRequireLoginDialog() {
        RequireLoginDialogFragment().show(supportFragmentManager, TAG_REQUIRE_LOGIN_DIALOG)
    }

    private fun enterEditMode() {
        viewModel.apply {
            updateCurrentScreenMode(EditMode)
            saveCurrentContents()
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

                    viewModel.updateCourseDetailContents(courseDetail.toCourseDetailContents())
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

    private fun <T : Any> T.toCourseDetailContents(): EditableCourseDetail? {
        return when (this) {
            is CourseDetail -> EditableCourseDetail(title, description)
            is EditableCourseDetail -> EditableCourseDetail(title, description)
            else -> null
        }
    }

    private fun initDepartureLatLng() {
        departureLatLng = LatLng(courseDetail.path[0][0], courseDetail.path[0][1])
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
                        viewModel.updateCourseDetailContents(response.toCourseDetailContents())
                        updateTextView(response)
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

    private fun updateTextView(courseDetail: EditableCourseDetail) {
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
            if (state is UiStateV2.Failure) {
                showSnackbar(binding.root, state.msg)
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

        private const val POPUP_MENU_X_OFFSET = 17
        private const val POPUP_MENU_Y_OFFSET = -10
        private const val TAG_MY_UPLOAD_COURSE_DELETE_DIALOG = "MY_UPLOAD_COURSE_DELETE_DIALOG"
        private const val TAG_MY_UPLOAD_COURSE_EDIT_DIALOG = "MY_UPLOAD_COURSE_EDIT_DIALOG"
        private const val TAG_REQUIRE_LOGIN_DIALOG = "REQUIRE_LOGIN_DIALOG"
    }
}