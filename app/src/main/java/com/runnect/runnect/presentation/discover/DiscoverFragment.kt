package com.runnect.runnect.presentation.discover

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentDiscoverBinding
import com.runnect.runnect.domain.entity.DiscoverBanner
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.MainActivity.Companion.isVisitorMode
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.discover.adapter.BannerAdapter
import com.runnect.runnect.presentation.discover.adapter.multiview.DiscoverMultiViewAdapter
import com.runnect.runnect.presentation.discover.adapter.multiview.DiscoverMultiViewType
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.presentation.discover.pick.DiscoverPickActivity
import com.runnect.runnect.presentation.discover.search.DiscoverSearchActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.presentation.storage.StorageScrapFragment
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_DATE
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_SCRAP
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_TRY_BANNER
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_UPLOAD_BUTTON
import com.runnect.runnect.util.custom.toast.RunnectToast
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.getCompatibleParcelableExtra
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showWebBrowser
import com.runnect.runnect.util.extension.viewLifeCycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover) {
    private val viewModel: DiscoverViewModel by viewModels()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var bannerScrollJob: Job
    private var currentBannerPosition = 0
    private var bannerItemCount = 0

    private lateinit var multiViewAdapter: DiscoverMultiViewAdapter
    private var isFromStorageScrap = StorageScrapFragment.isFromStorageScrap

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 상세페이지 갔다가 이전으로 돌아오면 아이템 변경사항이 바로 반영되도록 (제목, 스크랩)
                val updatedCourse: EditableDiscoverCourse =
                    result.data?.getCompatibleParcelableExtra(EXTRA_EDITABLE_DISCOVER_COURSE)
                        ?: return@registerForActivityResult

                if (updatedCourse.isDeleted) {
                    refreshDiscoverCourses()
                } else {
                    multiViewAdapter.updateCourseItem(
                        publicCourseId = viewModel.clickedCourseId,
                        updatedCourse = updatedCourse
                    )
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initView()
        createBannerScrollJob()
        addListener()
        addObserver()
        registerCallback()
    }

    private fun initView() {
        initMultiViewAdapter()
    }

    private fun initMultiViewAdapter() {
        multiViewAdapter = DiscoverMultiViewAdapter(
            onHeartButtonClick = { courseId, scrap ->
                viewModel.postCourseScrap(courseId, scrap)
            },
            onCourseItemClick = { courseId ->
                navigateToDetailScreen(courseId)
                viewModel.saveClickedCourseId(courseId)
            },
            handleVisitorMode = {
                context?.let { showCourseScrapWarningToast(it) }
            },
            onSortButtonClick = { criteria ->
                viewModel.sortRecommendCourses(criteria)
                Analytics.logClickedItemEvent(returnEventName(criteria))
            }
        ).apply {
            binding.rvDiscoverMultiView.adapter = this
        }
    }

    private fun returnEventName(criteria: String): String {
        var eventName = ""
        when (criteria) {
            "date" -> eventName = EVENT_CLICK_DATE
            "scrap" -> eventName = EVENT_CLICK_SCRAP
        }
        return eventName
    }

    private fun navigateToDetailScreen(publicCourseId: Int) {
        val context = context ?: return
        Intent(context, CourseDetailActivity::class.java).apply {
            putExtra(EXTRA_PUBLIC_COURSE_ID, publicCourseId)
            putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.COURSE_DISCOVER)
            resultLauncher.launch(this)
        }
        activity?.applyScreenEnterAnimation()
    }

    private fun showCourseScrapWarningToast(context: Context) {
        RunnectToast.createToast(
            context = context,
            message = context.getString(R.string.visitor_mode_course_detail_scrap_warning_msg)
        ).show()
    }

    private fun createBannerScrollJob() {
        bannerScrollJob = viewLifeCycleScope.launch {
            delay(BANNER_SCROLL_DELAY_TIME)
            binding.vpDiscoverBanner.setCurrentItem(++currentBannerPosition, true)
        }
    }

    private fun registerCallback() {
        registerBannerPageChangeCallback()
        registerBackPressedCallback()
        registerRefreshLayoutScrollUpCallback()
    }

    private fun registerBannerPageChangeCallback() {
        binding.vpDiscoverBanner.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateBannerPosition(position)
                updateBannerIndicatorPosition()
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                controlBannerScrollJob(state)
            }
        })
    }

    private fun updateBannerPosition(position: Int) {
        currentBannerPosition = position
    }

    private fun updateBannerIndicatorPosition() {
        if (bannerItemCount != 0) {
            val actualPosition = currentBannerPosition % bannerItemCount
            binding.indicatorDiscoverBanner.animatePageSelected(actualPosition)
        }
    }

    private fun controlBannerScrollJob(state: Int) {
        when (state) {
            ViewPager2.SCROLL_STATE_IDLE -> {
                if (!bannerScrollJob.isActive) createBannerScrollJob()
            }

            ViewPager2.SCROLL_STATE_DRAGGING -> {
                if (bannerScrollJob.isActive) bannerScrollJob.cancel()
            }

            ViewPager2.SCROLL_STATE_SETTLING -> {}
        }
    }

    private fun addListener() {
        initSearchButtonClickListener()
        initUploadButtonClickListener()
        initRefreshLayoutListener()
        initRecyclerViewScrollListener()
        initAppBarOffsetChangedListener()
    }

    private fun initRecyclerViewScrollListener() {
        binding.rvDiscoverMultiView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val isScrollDown = dy > 0
                if (isScrollDown) showCircleUploadButton()

                if (checkNextPageLoadingCondition(recyclerView)) {
                    viewModel.getRecommendCourseNextPage()
                }
            }
        })
    }

    private fun checkNextPageLoadingCondition(recyclerView: RecyclerView) =
        isCourseLoadingCompleted() && !recyclerView.canScrollVertically(SCROLL_DIRECTION) && !viewModel.isNextPageLoading()

    private fun isCourseLoadingCompleted() = ::multiViewAdapter.isInitialized &&
            multiViewAdapter.itemCount >= DiscoverMultiViewType.values().size

    private fun showCircleUploadButton() {
        binding.fabDiscoverUploadText.isVisible = false
        binding.fabDiscoverUpload.isVisible = true
    }

    private fun initAppBarOffsetChangedListener() {
        binding.appBarDiscover.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isAppbarExtended = verticalOffset == 0
            if (isAppbarExtended) showExtendedUploadButton()
        }
    }

    private fun showExtendedUploadButton() {
        binding.fabDiscoverUploadText.isVisible = true
        binding.fabDiscoverUpload.isVisible = false
    }

    private fun initRefreshLayoutListener() {
        binding.refreshLayout.setOnRefreshListener {
            refreshDiscoverCourses()
            binding.refreshLayout.isRefreshing = false
        }
    }

    fun refreshDiscoverCourses() {
        // 리프레시 직후에 비어있는 리스트로 리사이클러뷰 초기화
        multiViewAdapter.initMarathonCourses(emptyList())
        multiViewAdapter.initRecommendCourses(emptyList())

        // 첫 페이지 데이터로 리사이클러뷰 초기화
        viewModel.refreshDiscoverCourses()
    }

    private fun initSearchButtonClickListener() {
        val context = context ?: return
        binding.ivDiscoverSearch.setOnClickListener {
            startActivity(Intent(context, DiscoverSearchActivity::class.java))
            activity?.applyScreenEnterAnimation()
        }
    }

    private fun initUploadButtonClickListener() {
        binding.fabDiscoverUpload.setOnClickListener {
            navigateToCourseUploadScreen()
        }

        binding.fabDiscoverUploadText.setOnClickListener {
            navigateToCourseUploadScreen()
        }
    }

    private fun navigateToCourseUploadScreen() {
        val context = context ?: return
        if (isVisitorMode) {
            showCourseUploadWarningToast(context)
            return
        }
        Analytics.logClickedItemEvent(EVENT_CLICK_UPLOAD_BUTTON)
        startActivity(Intent(context, DiscoverPickActivity::class.java))
        activity?.applyScreenEnterAnimation()
    }

    private fun showCourseUploadWarningToast(context: Context) {
        RunnectToast.createToast(
            context,
            getString(R.string.visitor_mode_course_discover_upload_warning_msg)
        ).show()
    }

    private fun addObserver() {
        setupBannerGetStateObserver()
        setupMarathonCourseGetStateObserver()
        setupRecommendCourseGetStateObserver()
        setupRecommendCourseNextPageStateObserver()
        setupRecommendCourseSortStateObserver()
        setupCourseScrapStateObserver()
    }

    private fun setupBannerGetStateObserver() {
        viewModel.bannerGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val banners = state.data
                    initBannerViewPager(banners)
                }

                is UiStateV2.Failure -> {
                    context?.showSnackbar(
                        anchorView = binding.root,
                        message = state.msg,
                        gravity = Gravity.TOP
                    )
                }

                else -> {}
            }
        }
    }

    private fun initBannerViewPager(banners: List<DiscoverBanner>) {
        initBannerViewPagerAdapter(banners = banners)
        initBannerViewPagerItemPosition()
        initBannerViewPagerIndicator(banners = banners)
    }

    private fun initBannerViewPagerAdapter(banners: List<DiscoverBanner>) {
        bannerAdapter = BannerAdapter(
            banners = banners,
            onBannerItemClick = { url ->
                showPromotionWebsite(url)
                Analytics.logClickedItemEvent(EVENT_CLICK_TRY_BANNER)
            }
        ).apply {
            binding.vpDiscoverBanner.adapter = this
        }
    }

    private fun showPromotionWebsite(url: String) {
        if (url.isNotBlank()) {
            context?.showWebBrowser(url)
        }
    }

    private fun initBannerViewPagerItemPosition() {
        currentBannerPosition = CENTER_POS_OF_INFINITE_BANNERS
        binding.vpDiscoverBanner.setCurrentItem(currentBannerPosition, false)
    }

    private fun initBannerViewPagerIndicator(banners: List<DiscoverBanner>) {
        binding.indicatorDiscoverBanner.apply {
            bannerItemCount = banners.size
            createIndicators(bannerItemCount, 0)
        }
    }

    private fun setupMarathonCourseGetStateObserver() {
        viewModel.marathonCourseGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    multiViewAdapter.initMarathonCourses(state.data)
                }

                is UiStateV2.Failure -> {
                    context?.showSnackbar(
                        anchorView = binding.root,
                        message = state.msg,
                        gravity = Gravity.TOP
                    )
                }

                else -> {}
            }
        }
    }

    private fun setupRecommendCourseGetStateObserver() {
        viewModel.recommendCourseGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Loading -> showLoadingProgressBar()

                is UiStateV2.Success -> {
                    // todo: 리프레시에 의한 추천코스 어댑터의 submitList 동작이 완료되고 나서
                    multiViewAdapter.initRecommendCourses(state.data)

                    // todo: 로딩 프로그레스바를 삭제해야 한다.
                    dismissLoadingProgressBar()
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                    context?.showSnackbar(
                        anchorView = binding.root,
                        message = state.msg,
                        gravity = Gravity.TOP
                    )
                }

                else -> {}
            }
        }
    }

    private fun showLoadingProgressBar() {
        binding.rvDiscoverMultiView.isVisible = false
        binding.pbDiscoverLoading.isVisible = true
    }

    private fun dismissLoadingProgressBar() {
        binding.rvDiscoverMultiView.isVisible = true
        binding.pbDiscoverLoading.isVisible = false
    }

    private fun setupRecommendCourseNextPageStateObserver() {
        viewModel.recommendCourseNextPageState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    multiViewAdapter.addRecommendCourseNextPage(state.data)
                }

                is UiStateV2.Failure -> {
                    context?.showSnackbar(
                        anchorView = binding.root,
                        message = state.msg,
                        gravity = Gravity.TOP
                    )
                }

                else -> {}
            }
        }
    }

    private fun setupRecommendCourseSortStateObserver() {
        viewModel.recommendCourseSortState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    multiViewAdapter.sortRecommendCourseFirstPage(state.data)
                }

                is UiStateV2.Failure -> {
                    context?.showSnackbar(
                        anchorView = binding.root,
                        message = state.msg,
                        gravity = Gravity.TOP
                    )
                }

                else -> {}
            }
        }
    }

    private fun setupCourseScrapStateObserver() {
        viewModel.courseScrapState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val response = state.data ?: return@observe
                    multiViewAdapter.updateCourseScrap(
                        publicCourseId = response.publicCourseId.toInt(),
                        scrap = response.scrapTF
                    )
                }

                is UiStateV2.Failure -> {
                    context?.showSnackbar(
                        anchorView = binding.root,
                        message = state.msg,
                        gravity = Gravity.TOP
                    )
                }

                else -> {}
            }
        }
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkFromStorageScrap()
                activity?.navigateToPreviousScreenWithAnimation()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    private fun checkFromStorageScrap() {
        if (isFromStorageScrap) {
            StorageScrapFragment.isFromStorageScrap = false
            MainActivity.updateStorageScrapScreen()
        }
    }

    private fun registerRefreshLayoutScrollUpCallback() {
        binding.refreshLayout.setOnChildScrollUpCallback { _, _ ->
            checkRefreshPossibleCondition()
        }
    }

    // 첫번째 멀티 뷰 타입이 완전히 보일 때만 당겨서 리프레시 가능하도록
    private fun checkRefreshPossibleCondition(): Boolean {
        val layoutManager = binding.rvDiscoverMultiView.layoutManager as LinearLayoutManager
        return layoutManager.findFirstCompletelyVisibleItemPosition() > 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            MainActivity.discoverFragment = this
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.discoverFragment = null
    }

    companion object {
        private const val BANNER_SCROLL_DELAY_TIME = 5000L
        private const val CENTER_POS_OF_INFINITE_BANNERS = Int.MAX_VALUE / 2
        private const val SCROLL_DIRECTION = 1
        private const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        private const val EXTRA_ROOT_SCREEN = "rootScreen"
        const val EXTRA_EDITABLE_DISCOVER_COURSE = "editable_discover_course"
    }
}