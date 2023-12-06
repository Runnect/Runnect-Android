package com.runnect.runnect.presentation.discover

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentDiscoverBinding
import com.runnect.runnect.domain.entity.EditableDiscoverCourse
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
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover) {
    private val viewModel: DiscoverViewModel by viewModels()
    private val bannerAdapter by lazy { BannerAdapter { showPromotionWebsite(it) } }
    private lateinit var multiViewAdapter: DiscoverMultiViewAdapter

    private var isFromStorageScrap = StorageScrapFragment.isFromStorageScrap

    // todo: 프로모션 배너 관련 변수들
    private lateinit var scrollHandler: Handler
    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var scrollPageRunnable: Runnable
    private lateinit var bannerPageChangeCallback: ViewPager2.OnPageChangeCallback
    private var currentPosition = PAGE_NUM / 2

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedCourse: EditableDiscoverCourse =
                    result.data?.getCompatibleParcelableExtra(KEY_EDITABLE_DISCOVER_COURSE)
                        ?: return@registerForActivityResult
                //recommendCourseAdapter.updateRecommendItem(viewModel.clickedCourseId, updatedCourse)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initLayout()
        addListener()
        addObserver()
        registerBackPressedCallback()
        registerRefreshLayoutScrollUpCallback()
    }

    private fun initLayout() {
        binding.vpDiscoverBanner.apply {
            initCurrentBannerPage(this)
            initBannerTimer(this)
        }
    }

    private fun registerRefreshLayoutScrollUpCallback() {
        // 첫번째 멀티 뷰 타입이 완전히 화면에 보일 때만 리프레시 가능하도록
        val layoutManager = binding.rvDiscoverMultiView.layoutManager as LinearLayoutManager
        binding.refreshLayout.setOnChildScrollUpCallback { _, _ ->
            layoutManager.findFirstCompletelyVisibleItemPosition() > 0
        }
    }

    fun getRecommendCourses(pageNo: Int) {
        viewModel.getRecommendCourse(pageNo = pageNo, "date")
    }

    private fun showPromotionWebsite(url: String) {
        if (url.isNotBlank()) {
            requireContext().showWebBrowser(url)
        }
    }

    private fun navigateToDetailScreen(publicCourseId: Int) {
        val intent = Intent(requireContext(), CourseDetailActivity::class.java).apply {
            putExtra(EXTRA_PUBLIC_COURSE_ID, publicCourseId)
            putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.COURSE_DISCOVER)
        }
        resultLauncher.launch(intent)
        requireActivity().applyScreenEnterAnimation()
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleFromStorageScrap()
                requireActivity().navigateToPreviousScreenWithAnimation()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun handleFromStorageScrap() {
        if (isFromStorageScrap) {
            StorageScrapFragment.isFromStorageScrap = false
            MainActivity.updateStorageScrapScreen()
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

                // 스크롤을 내리면 원형 버튼이 보이도록
                if (dy > 0) {
                    binding.fabDiscoverUploadText.isVisible = false
                    binding.fabDiscoverUpload.isVisible = true
                }
            }
        })
    }

    private fun initAppBarOffsetChangedListener() {
        // CollapsingToolbarLayout의 높이가 완전히 확장되면 텍스트가 포함된 버튼이 보이도록
        binding.appBarDiscover.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                binding.fabDiscoverUploadText.isVisible = true
                binding.fabDiscoverUpload.isVisible = false
            }
        }
    }

    private fun initRefreshLayoutListener() {
        binding.refreshLayout.setOnRefreshListener {
            viewModel.resetMultiViewItems()
            viewModel.refreshCurrentCourses()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun initSearchButtonClickListener() {
        binding.ivDiscoverSearch.setOnClickListener {
            startActivity(Intent(requireContext(), DiscoverSearchActivity::class.java))
            requireActivity().applyScreenEnterAnimation()
        }
    }

    private fun initUploadButtonClickListener() {
        binding.fabDiscoverUpload.setOnClickListener {
            if (isVisitorMode) {
                showCourseUploadWarningToast()
                return@setOnClickListener
            }

            startActivity(Intent(requireContext(), DiscoverPickActivity::class.java))
            requireActivity().applyScreenEnterAnimation()
        }

        binding.fabDiscoverUploadText.setOnClickListener {
            if (isVisitorMode) {
                showCourseUploadWarningToast()
                return@setOnClickListener
            }

            startActivity(Intent(requireContext(), DiscoverPickActivity::class.java))
            requireActivity().applyScreenEnterAnimation()
        }
    }

    private fun showCourseUploadWarningToast() {
        RunnectToast.createToast(
            requireContext(),
            getString(R.string.visitor_mode_course_discover_upload_warning_msg)
        ).show()
    }

    private fun addObserver() {
        setupBannerGetStateObserver()
        setupCourseLoadStateObserver()
        setupCourseScrapStateObserver()
    }

    private fun setupBannerGetStateObserver() {
        viewModel.bannerGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    initBannerViewPager()
                    val banners = state.data
                    bannerAdapter.submitList(banners)

                }

                is UiStateV2.Failure -> {
                    requireContext().showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun setupCourseLoadStateObserver() {
        viewModel.courseLoadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Loading -> showLoadingProgressBar()

                is UiStateV2.Success -> {
                    dismissLoadingProgressBar()
                    initMultiViewAdapter()
                    initMultiRecyclerView()
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                    requireContext().showSnackbar(binding.root, state.msg)
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
            currentPageNumber = viewModel.currentPageNumber,
            onNextPageLoad = { pageNo ->
                // todo: 다음 페이지 요청하고, 뷰 갱신하기
                viewModel.getRecommendCourse(pageNo, "date")
                viewModel.updateCurrentPageNumber(pageNo)
            }
        )
    }

    private fun initMultiRecyclerView() {
        binding.rvDiscoverMultiView.apply {
            setHasFixedSize(true)
            adapter = multiViewAdapter
        }
    }

    private fun setupCourseScrapStateObserver() {
        viewModel.courseScrapState.observe(viewLifecycleOwner) { state ->
            if (state is UiStateV2.Failure) {
                requireContext().showSnackbar(binding.root, state.msg)
            }
        }
    }

    /** 배너 데이터 목록 초기화 */

    private fun initBannerViewPager() {
        initBannerAdapter()
        bannerAdapter.setBannerCount(viewModel.bannerCount)
        binding.vpDiscoverBanner.apply {
            initViewPagerIndicator(this)
            registerBannerPageChangeCallback(this)
        }
    }

    private fun initBannerAdapter() {
        binding.vpDiscoverBanner.adapter = bannerAdapter
    }

    private fun initViewPagerIndicator(viewPager: ViewPager2) {
        binding.ciDiscoverBanner.apply {
            setViewPager(viewPager)
            createIndicators(viewModel.bannerCount, PAGE_NUM / 2)
        }
    }

    private fun registerBannerPageChangeCallback(viewPager: ViewPager2) {
        bannerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.ciDiscoverBanner.animatePageSelected(position % viewModel.bannerCount)
                currentPosition = position
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    binding.ciDiscoverBanner.animatePageSelected(position % viewModel.bannerCount)
                }
            }
        }
        viewPager.registerOnPageChangeCallback(bannerPageChangeCallback)
    }

    /** 배너 레이아웃 초기화 */

    private fun initCurrentBannerPage(viewPager: ViewPager2) {
        viewPager.setCurrentItem(PAGE_NUM / 2, false)
    }

    private fun initBannerTimer(viewPager: ViewPager2) {
        initScrollHandler()
        initScrollPageRunnable(viewPager)
        initTimerTask()
    }

    private fun initScrollHandler() {
        scrollHandler = Handler(Looper.getMainLooper())
        timer = Timer()
    }

    private fun initScrollPageRunnable(viewPager: ViewPager2) {
        scrollPageRunnable = Runnable {
            viewPager.setCurrentItem(++currentPosition, true)
        }
    }

    private fun initTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                scrollHandler.post(scrollPageRunnable)
            }
        }
    }

    private fun autoScrollStart() {
        if (::timer.isInitialized && timerTask.scheduledExecutionTime() <= 0) {
            timer.schedule(timerTask, INTERVAL_TIME, INTERVAL_TIME)
        }
    }

    private fun autoScrollStop() {
        if (::timer.isInitialized && timerTask.scheduledExecutionTime() <= 0) {
            timerTask.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        autoScrollStart()
    }

    override fun onPause() {
        super.onPause()
        autoScrollStop()
        binding.vpDiscoverBanner.unregisterOnPageChangeCallback(bannerPageChangeCallback)
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
        private const val PAGE_NUM = 900
        private const val INTERVAL_TIME = 5000L
        private const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        private const val EXTRA_ROOT_SCREEN = "rootScreen"
        const val KEY_EDITABLE_DISCOVER_COURSE = "editable_discover_course"
        const val END_PAGE = "HTTP 400 Bad Request"
    }
}