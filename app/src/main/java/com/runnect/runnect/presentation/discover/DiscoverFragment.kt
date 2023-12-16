package com.runnect.runnect.presentation.discover

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.MainActivity.Companion.isVisitorMode
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.discover.adapter.BannerAdapter
import com.runnect.runnect.presentation.discover.adapter.DiscoverMultiViewAdapter
import com.runnect.runnect.presentation.discover.pick.DiscoverPickActivity
import com.runnect.runnect.presentation.discover.search.DiscoverSearchActivity
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.presentation.storage.StorageScrapFragment
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
                val updatedCourse: EditableDiscoverCourse =
                    result.data?.getCompatibleParcelableExtra(EXTRA_EDITABLE_DISCOVER_COURSE)
                        ?: return@registerForActivityResult

                // todo: 상세페이지 갔다가 이전으로 돌아오면 제목, 스크랩 변경사항이 바로 표시되도록
                multiViewAdapter.updateCourseItem(
                    publicCourseId = viewModel.clickedCourseId,
                    updatedCourse = updatedCourse
                )
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        createBannerScrollJob()
        addListener()
        addObserver()
        registerCallback()
    }

    private fun createBannerScrollJob() {
        bannerScrollJob = viewLifeCycleScope.launch {
            delay(BANNER_SCROLL_DELAY_TIME)
            binding.vpDiscoverBanner.setCurrentItem(++currentBannerPosition, true)
        }
    }

    private fun initBannerViewPager(banners: List<DiscoverBanner>) {
        initBannerViewPagerAdapter(banners)
        initBannerViewPagerItemPosition()
        initBannerViewPagerIndicator(banners)
    }

    private fun initBannerViewPagerAdapter(banners: List<DiscoverBanner>) {
        bannerAdapter = BannerAdapter(
            banners = banners,
            onBannerItemClick = { url ->
                showPromotionWebsite(url)
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
        currentBannerPosition = Int.MAX_VALUE / 2
        binding.vpDiscoverBanner.setCurrentItem(currentBannerPosition, false)
    }

    private fun initBannerViewPagerIndicator(banners: List<DiscoverBanner>) {
        binding.indicatorDiscoverBanner.apply {
            bannerItemCount = banners.size
            createIndicators(bannerItemCount, 0)
        }
    }

    private fun registerCallback() {
        registerBannerPageChangeCallback()
        registerBackPressedCallback()
        registerRefreshLayoutScrollUpCallback()
    }

    private fun registerBannerPageChangeCallback() {
        binding.vpDiscoverBanner.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Timber.d("viewpager position: $position")
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
            }
        })
    }

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
            viewModel.resetMultiViewItems()
            viewModel.refreshCurrentCourses()
            binding.refreshLayout.isRefreshing = false
        }
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
                    context?.showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun setupMarathonCourseGetStateObserver() {
        viewModel.marathonCourseState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Loading -> showLoadingProgressBar()

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                    context?.showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun setupRecommendCourseGetStateObserver() {
        viewModel.recommendCourseState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Loading -> showLoadingProgressBar()

                is UiStateV2.Success -> {
                    if (viewModel.checkCourseLoadState()) {
                        dismissLoadingProgressBar()
                        initMultiViewAdapter()
                        initMultiRecyclerView()
                    }
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                    context?.showSnackbar(binding.root, state.msg)
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

    private fun initMultiViewAdapter() {
        multiViewAdapter = DiscoverMultiViewAdapter(
            multiViewItems = viewModel.multiViewItems,
            onHeartButtonClick = { courseId, scrap ->
                viewModel.postCourseScrap(courseId, scrap)
            },
            onCourseItemClick = { courseId ->
                navigateToDetailScreen(courseId)
                viewModel.saveClickedCourseId(courseId)
            },
            handleVisitorMode = {
                context?.let { showCourseScrapWarningToast(it) }
            }
        )
    }

    private fun initMultiRecyclerView() {
        binding.rvDiscoverMultiView.apply {
            setHasFixedSize(true)
            adapter = multiViewAdapter
        }
    }

    private fun navigateToDetailScreen(publicCourseId: Int) {
        val context = context ?: return
        val intent = Intent(context, CourseDetailActivity::class.java).apply {
            putExtra(EXTRA_PUBLIC_COURSE_ID, publicCourseId)
            putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.COURSE_DISCOVER)
        }
        resultLauncher.launch(intent)
        activity?.applyScreenEnterAnimation()
    }

    private fun showCourseScrapWarningToast(context: Context) {
        RunnectToast.createToast(
            context = context,
            message = context.getString(R.string.visitor_mode_course_detail_scrap_warning_msg)
        ).show()
    }

    private fun setupCourseScrapStateObserver() {
        viewModel.courseScrapState.observe(viewLifecycleOwner) { state ->
            if (state is UiStateV2.Failure) {
                context?.showSnackbar(binding.root, state.msg)
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

    fun getRecommendCourses(pageNo: Int) {
        viewModel.getRecommendCourse(pageNo = pageNo, ordering = "date")
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
        private const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        private const val EXTRA_ROOT_SCREEN = "rootScreen"
        const val EXTRA_EDITABLE_DISCOVER_COURSE = "editable_discover_course"
    }
}