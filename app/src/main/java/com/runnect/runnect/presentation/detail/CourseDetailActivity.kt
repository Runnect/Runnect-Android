package com.runnect.runnect.presentation.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.mypage.upload.MyUploadActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.custom.CommonDialogFragment
import com.runnect.runnect.util.custom.PopupItem
import com.runnect.runnect.util.custom.RunnectPopupMenu
import com.runnect.runnect.util.custom.RunnectToast
import com.runnect.runnect.util.extension.*
import com.runnect.runnect.util.mode.ScreenMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_edit_mode.*
import kotlinx.android.synthetic.main.custom_dialog_make_course.view.*
import kotlinx.android.synthetic.main.custom_dialog_require_login.view.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class CourseDetailActivity :
    BindingActivity<ActivityCourseDetailBinding>(R.layout.activity_course_detail) {
    private val viewModel: CourseDetailViewModel by viewModels()
    private lateinit var departureLatLng: LatLng
    private lateinit var deleteDialog: AlertDialog
    private lateinit var editBottomSheet: BottomSheetDialog
    private lateinit var editInterruptDialog: AlertDialog

    private val rootScreen by lazy { intent.getStringExtra(EXTRA_ROOT).toString() }
    private var publicCourseId by Delegates.notNull<Int>()

    private var isVisitorMode: Boolean = MainActivity.isVisitorMode
    private var isFromDeepLink: Boolean = false
    private val touchList = arrayListOf<LatLng>()
    private var currentScreenMode: ScreenMode = ScreenMode.ReadOnlyMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        initDetailCourseId()
        initDeepLink()

        addListener()
        addObserver()
        registerBackPressedCallback()

        initEditBottomSheet()
        initDeleteDialog()
        setDeleteDialogClickEvent()
        initEditInterruptedDialog()
        setEditInterruptedDialog()
    }

    private fun initDetailCourseId() {
        publicCourseId = intent.getIntExtra(EXTRA_PUBLIC_COURSE_ID, 0)
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackButtonFromDeepLink()
                handleBackButtonByCurrentScreenMode()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun handleBackButtonFromDeepLink() {
        if (isFromDeepLink) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION, "fromCourseDetail")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            isFromDeepLink = false
        }
    }

    private fun navigateToPreviousScreen() {
        when (rootScreen) {
            CourseDetailRootScreen.COURSE_STORAGE_SCRAP.extraName ->
                handleReturnToStorageScrap()

            CourseDetailRootScreen.COURSE_DISCOVER.extraName ->
                handleReturnToDiscover()

            CourseDetailRootScreen.MY_PAGE_UPLOAD_COURSE.extraName ->
                handleReturnToMyUpload()
        }

        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

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

    private fun initDeepLink() {
        if (Intent.ACTION_VIEW == intent.action) { //딥링크를 통해 열린 경우
            isFromDeepLink = true
            val uri = intent.data
            if (uri != null) {
                // 여기서 androidExecutionParams 값들을 받아와 어떠한 상세페이지를 띄울지 결정할 수 있음
                publicCourseId = uri.getQueryParameter("publicCourseId")!!.toInt()
                Timber.tag("deeplink-publicCourseId").d("$publicCourseId")
            }
        }
        //위의 if문을 작성해줌으로써 어떤 경우에도 publicCourseId 값이 세팅이 돼있어 getCourseDetail()을 돌려줄 수 있습니다.
        getCourseDetail()
    }

    private fun addListener() {
        initBackButtonClickListener()

        initScrapButtonClickListener()

        initRunStartButtonClickListener()

        initShareButtonClickListener()

        initEditFinishButtonClickListener()

        initShowMoreButtonClickListener()
    }

    private fun initShowMoreButtonClickListener() {
        binding.btnShowMore.setOnClickListener { view ->
            if (isMyUploadCourseDetailScreen()) {
//                editBottomSheet.show()
                // todo: 수정/삭제 팝업메뉴 띄우기
                showPopupMenu(view)
            } else {
//                bottomSheet()
                // todo: 신고하기 팝업메뉴 띄우기
            }
        }
    }

    private fun initEditFinishButtonClickListener() {
        binding.tvCourseDetailEditFinish.setOnClickListener {
            viewModel.patchUpdatePublicCourse(publicCourseId)
        }
    }

    private fun initShareButtonClickListener() {
        binding.btnShare.setOnClickListener {
            sendKakaoLink(
                title = viewModel.title.value.toString(),
                desc = viewModel.description.value.toString(),
                image = viewModel.imageUrl.value.toString()
            )
        }
    }

    private fun initRunStartButtonClickListener() {
        binding.btnCourseDetailFinish.setOnClickListener {
            if (isVisitorMode) {
                requireLogin()
            } else {
                val intent = Intent(this, CountDownActivity::class.java).apply {
                    putExtra(
                        EXTRA_COURSE_DATA, CourseData(
                            courseId = viewModel.courseDetail.courseId,
                            publicCourseId = viewModel.courseDetail.id,
                            touchList = touchList,
                            startLatLng = departureLatLng,
                            departure = viewModel.courseDetail.departure,
                            distance = viewModel.courseDetail.distance.toFloat(),
                            image = viewModel.courseDetail.image,
                            dataFrom = "detail"
                        )
                    )
                }
                startActivity(intent)
            }
        }
    }

    private fun initScrapButtonClickListener() {
        binding.ivCourseDetailScrap.setOnClickListener {
            if (isVisitorMode) {
                RunnectToast.createToast(
                    this@CourseDetailActivity,
                    "러넥트에 가입하면 코스를 스크랩할 수 있어요"
                ).show()
            } else {
                it.isSelected = !it.isSelected
                viewModel.postCourseScrap(publicCourseId, it.isSelected)
                viewModel.isEdited = true
            }
        }
    }

    private fun initBackButtonClickListener() {
        binding.ivCourseDetailBack.setOnClickListener {
            handleBackButtonFromDeepLink()
            handleBackButtonByCurrentScreenMode()

//            if (viewModel.editMode.value == true) {
//                editInterruptDialog.show()
//                return@setOnClickListener
//            }
//            if (!viewModel.isEdited) {
//                finish()
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                return@setOnClickListener
//            }
        }
    }

    private fun handleBackButtonByCurrentScreenMode() {
        when(currentScreenMode) {
            is ScreenMode.ReadOnlyMode -> navigateToPreviousScreen()
            is ScreenMode.EditMode -> showStopEditingDialog()
        }
    }

    private fun showStopEditingDialog() {
        val dialog = CommonDialogFragment(
            stringOf(R.string.dialog_my_upload_course_detail_stop_editing_desc),
            stringOf(R.string.dialog_course_detail_stop_editing_no),
            stringOf(R.string.dialog_course_detail_stop_editing_yes),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = {
                // 편집 모드 -> 뒤로가기 버튼 -> 편집 중단 확인 -> 뷰에 원래 제목으로 보여줌.
                //viewModel.updateHistoryTitle(temporarilySavedTitle)
                enterReadMode()
            }
        )
        dialog.show(supportFragmentManager, TAG_MY_UPLOAD_COURSE_EDIT_DIALOG)
    }

    private fun showPopupMenu(anchorView: View) {
        val popupItems = listOf(
            PopupItem(R.drawable.ic_detail_more_edit, getString(R.string.popup_menu_item_edit)),
            PopupItem(R.drawable.ic_detail_more_delete, getString(R.string.popup_menu_item_delete))
        )

        RunnectPopupMenu(anchorView.context, popupItems) { _, _, pos ->
            when (pos) {
                0 -> enterEditMode()
                1 -> showMyUploadCourseDeleteDialog()
            }
        }.apply {
            showCustomPosition(anchorView)
        }
    }

    private fun showMyUploadCourseDeleteDialog() {
        val dialog = CommonDialogFragment(
            stringOf(R.string.dialog_my_upload_course_detail_delete_desc),
            stringOf(R.string.dialog_course_detail_delete_no),
            stringOf(R.string.dialog_course_detail_delete_yes),
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = { /** todo: 뷰모델에서 코스 삭제하기 */ }
        )
        dialog.show(supportFragmentManager, TAG_MY_UPLOAD_COURSE_DELETE_DIALOG)
    }

    private fun RunnectPopupMenu.showCustomPosition(anchorView: View) {
        showAsDropDown(anchorView, POPUP_MENU_X_OFFSET, POPUP_MENU_Y_OFFSET, Gravity.END)
    }


    private fun isMyUploadCourseDetailScreen() =
        rootScreen == CourseDetailRootScreen.MY_PAGE_UPLOAD_COURSE.extraName

    private fun requireLogin() {
        val (dialog, dialogLayout) = setActivityDialog(
            layoutInflater = layoutInflater,
            view = binding.root,
            resId = R.layout.custom_dialog_require_login,
            cancel = false
        )
        with(dialogLayout) {
            this.btn_login.setOnClickListener {
                val intent = Intent(this@CourseDetailActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                dialog.dismiss()
            }
            this.btn_cancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }


    private fun handleReturnToMyUpload() {
        val intent = Intent(this, MyUploadActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun handleReturnToDiscover() {
        MainActivity.updateDiscoverFragment()
    }

    private fun handleReturnToStorageScrap() {
        MainActivity.updateStorageScrap()
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
        viewModel.getCourseDetail(publicCourseId)
    }

    private fun setDepartureLatLng() {
        departureLatLng =
            LatLng(viewModel.courseDetail.path[0][0], viewModel.courseDetail.path[0][1])
    }

    private fun setTouchList() {
        for (i in 1 until viewModel.courseDetail.path.size) {
            touchList.add(
                LatLng(
                    viewModel.courseDetail.path[i][0], viewModel.courseDetail.path[i][1]
                )
            )
        }
    }

    private fun addObserver() {
        viewModel.isDeepLinkLogin.observe(this) {
            if (viewModel.isDeepLinkLogin.value == false) { //딥링크로 진입했는데 로그인이 안 돼있을 경우
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                viewModel.isDeepLinkLogin.value = true
            }
        }

        viewModel.courseDetailState.observe(this) { state ->
            if (state == UiState.Success) {
                with(binding) {
                    with(viewModel.courseDetail) {
                        val stampResId = this@CourseDetailActivity.getStampResId(
                            stampId = viewModel.stampId.value,
                            resNameParam = RES_NAME,
                            resType = RES_STAMP_TYPE,
                            packageName = packageName
                        )
                        ivCourseDetailProfileStamp.load(stampResId)
                        ivCourseDetailProfileNickname.text = nickname
                        if (level == "알 수 없음") {
                            tvCourseDetailProfileLv.isVisible = false
                            tvCourseDetailProfileLvIndicator.isVisible = false
                        } else {
                            tvCourseDetailProfileLv.text = level
                        }
                        ivCourseDetailScrap.isSelected = scrap

                    }

                }
                setDepartureLatLng()
                setTouchList()
            }
        }


        viewModel.myUploadDeleteState.observe(this)
        { state ->
            when (state) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    if (rootScreen == MY_UPLOAD_ACTIVITY_TAG) {
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
        viewModel.isEditFinishEnable.observe(this)
        {
            with(binding.tvCourseDetailEditFinish) {
                isActivated = it
                isClickable = it
            }
        }
        viewModel.courseUpdateState.observe(this)
        { state ->
            when (state) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> handleSuccessfulCourseUpdate()
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
                editBottomSheet.layout_edit_frame -> enterEditMode()
                editBottomSheet.layout_delete_frame -> deleteDialog.show()
            }
        }
    }

    private fun initDeleteDialog() {
        deleteDialog = setCustomDialog(
            layoutInflater, binding.root, DELETE_DIALOG_DESC, DELETE_DIALOG_YES_BTN
        )
    }

    private fun setDeleteDialogClickEvent() {
        deleteDialog.setDialogButtonClickListener { which ->
            when (which) {
                deleteDialog.btn_delete_yes -> viewModel.deleteUploadCourse(publicCourseId)
            }
        }
    }

    private fun initEditInterruptedDialog() {
        editInterruptDialog = setCustomDialog(
            layoutInflater = layoutInflater,
            view = binding.root,
            description = EDIT_INTERRUPT_DIALOG_DESC,
            yesBtnText = EDIT_INTERRUPT_DIALOG_YES_BTN,
            noBtnText = EDIT_INTERRUPT_DIALOG_NO_BTN
        )
    }

    private fun setEditInterruptedDialog() {
        editInterruptDialog.setDialogButtonClickListener { which ->
            when (which) {
                editInterruptDialog.btn_delete_yes -> enterReadMode()
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
            R.layout.custom_dialog_report, findViewById<LinearLayout>(R.id.bottomSheet)
        )
        // bottomSheetDialog의 dismiss 버튼 선택시 dialog disappear
        bottomSheetView.findViewById<View>(R.id.view_go_to_report_frame).setOnClickListener {
            startWebView(REPORT_URL)
            bottomSheetDialog.dismiss()
        }
        // bottomSheetDialog 뷰 생성
        bottomSheetDialog.setContentView(bottomSheetView)
        // bottomSheetDialog 호출
        bottomSheetDialog.show()
    }

    // 키보드 밖 터치 시, 키보드 내림
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)

            val x = ev!!.x.toInt()
            val y = ev.y.toInt()

            if (!rect.contains(x, y)) {
                hideKeyboard(focusView)
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    companion object {
        const val REPORT_URL =
            "https://docs.google.com/forms/d/e/1FAIpQLSek2rkClKfGaz1zwTEHX3Oojbq_pbF3ifPYMYezBU0_pe-_Tg/viewform"
        const val RES_NAME = "mypage_img_stamp_"
        const val RES_STAMP_TYPE = "drawable"
        const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        const val EXTRA_ROOT = "root"
        const val EXTRA_COURSE_DATA = "CourseData"
        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"

        // ---------------------------------------------------

        private const val STORAGE_SCRAP_TAG = "storageScrap"
        private const val COURSE_DISCOVER_TAG = "discover"
        private const val MY_UPLOAD_ACTIVITY_TAG = "upload"

        private const val POPUP_MENU_X_OFFSET = 17
        private const val POPUP_MENU_Y_OFFSET = -10

        private const val TAG_MY_UPLOAD_COURSE_DELETE_DIALOG = "MY_UPLOAD_COURSE_DELETE_DIALOG"
        private const val TAG_MY_UPLOAD_COURSE_EDIT_DIALOG = "MY_UPLOAD_COURSE_EDIT_DIALOG"
    }
}