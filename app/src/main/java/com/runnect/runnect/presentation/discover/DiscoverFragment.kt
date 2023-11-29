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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.domain.entity.PromotionBanner
import com.runnect.runnect.databinding.FragmentDiscoverBinding
import com.runnect.runnect.domain.entity.EditableDiscoverCourse
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.MainActivity.Companion.isVisitorMode
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.discover.adapter.DiscoverCourseAdapter
import com.runnect.runnect.presentation.discover.adapter.BannerAdapter
import com.runnect.runnect.presentation.discover.adapter.DiscoverScrollAdapter
import com.runnect.runnect.presentation.discover.pick.DiscoverPickActivity
import com.runnect.runnect.presentation.discover.search.DiscoverSearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.presentation.storage.StorageScrapFragment
import com.runnect.runnect.util.custom.toast.RunnectToast
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.listener.OnBannerItemClick
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.getCompatibleParcelableExtra
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showWebBrowser
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover),
    OnBannerItemClick {
    private val viewModel: DiscoverViewModel by viewModels()

    private val bannerAdapter: BannerAdapter by lazy { BannerAdapter(this) }
    private lateinit var marathonCourseAdapter: DiscoverCourseAdapter
    private lateinit var recommendCourseAdapter: DiscoverCourseAdapter
    private lateinit var scrollAdapter: DiscoverScrollAdapter

    // 프로모션 배너 관련 변수들
    private lateinit var scrollHandler: Handler
    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var scrollPageRunnable: Runnable
    private lateinit var pageRegisterCallback: ViewPager2.OnPageChangeCallback
    private var currentPosition = PAGE_NUM / 2

    private var isFromStorageScrap = StorageScrapFragment.isFromStorageScrap

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedCourse: EditableDiscoverCourse =
                    result.data?.getCompatibleParcelableExtra(KEY_EDITABLE_DISCOVER_COURSE)
                        ?: return@registerForActivityResult

                recommendCourseAdapter.updateRecommendItem(viewModel.clickedCourseId, updatedCourse)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initAdapter()
        initLayout()
        addListener()
        addObserver()
        registerBackPressedCallback()
        initRefreshLayoutListener()
    }

    fun getRecommendCourses(pageNo: Int) {
        viewModel.getRecommendCourse(pageNo = pageNo, "date")
    }

//    private fun initRecyclerViewScrollListener() {
//        binding.rvDiscoverRecommend.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                if (!binding.rvDiscoverRecommend.canScrollVertically(SCROLL_DIRECTION)) {
//                    viewModel.loadNextPage()
//                }
//            }
//        })
//    }

    private fun initAdapter() {
        marathonCourseAdapter = initDiscoverCourseAdapter()
        recommendCourseAdapter = initDiscoverCourseAdapter()
        scrollAdapter = DiscoverScrollAdapter(bannerAdapter, marathonCourseAdapter, recommendCourseAdapter)
    }

    private fun initDiscoverCourseAdapter() = DiscoverCourseAdapter(
        onHeartButtonClick = { courseId, scrap ->
            viewModel.postCourseScrap(id = courseId, scrapTF = scrap)
        },
        onCourseItemClick = { courseId ->
            navigateToDetailScreen(courseId)
            viewModel.saveClickedCourseId(courseId)
        }
    )

//    private fun initMarathonCourseRecyclerView() {
//        binding.rvDiscoverMarathon.apply {
//            setHasFixedSize(true)
//            adapter = marathonCourseAdapter
//            // todo: 아이템 간의 여백 조정
//        }
//    }

//    private fun initRecommendCourseRecyclerView() {
//        scrollBinding.rvDiscoverRecommend.apply {
//            setHasFixedSize(true)
//            layoutManager = GridLayoutManager(requireContext(), 2)
//            adapter = recommendCourseAdapter
//            addItemDecoration(
//                GridSpacingItemDecoration(
//                    context = requireContext(),
//                    spanCount = 2,
//                    horizontalSpacing = 6,
//                    topSpacing = 20
//                )
//            )
//        }
//    }

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

    private fun initLayout() {
        setPromotionBanner(scrollBinding.vpDiscoverBanner)
        initMarathonCourseRecyclerView()
        initRecommendCourseRecyclerView()
    }

    private fun addListener() {
        // todo: 스크롤 위치에 따라 다음 페이지 불러오기
        // initRecyclerViewScrollListener()

        initSearchButtonClickListener()
        initUploadButtonClickListener()
    }

    private fun initSearchButtonClickListener() {
        binding.ivDiscoverSearch.setOnClickListener {
            startActivity(Intent(requireContext(), DiscoverSearchActivity::class.java))
            requireActivity().applyScreenEnterAnimation()
        }
    }

    private fun initUploadButtonClickListener() {
        binding.btnDiscoverUpload.setOnClickListener {
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

    private fun initRefreshLayoutListener() {
        binding.refreshLayout.setOnRefreshListener {
            // 기존에 조회했던 리스트에 아이템이 누적으로 더해지므로 clear()로 비워주었다.
            //viewModel.recommendCourses.clear()
            viewModel.getRecommendCourse(pageNo = 1, "date")
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun addObserver() {
        setupBannerGetStateObserver()
        setupMarathonCourseGetStateObserver()
        setupRecommendCourseGetStateObserver()
        setupCourseScrapStateObserver()
    }

    private fun setupBannerGetStateObserver() {
        viewModel.bannerGetState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Success -> {
                    setBannerData()
                }

                UiState.Failure -> {}

                else -> {}
            }
        }
    }

    private fun setupMarathonCourseGetStateObserver() {
        viewModel.marathonCourseGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val courses = state.data ?: return@observe
                    marathonCourseAdapter.submitList(courses)
                }

                is UiStateV2.Failure -> {
                    requireContext().showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun setupRecommendCourseGetStateObserver() {
        viewModel.recommendCourseGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val courses = state.data ?: return@observe
                    recommendCourseAdapter.submitList(courses)
                }

                is UiStateV2.Failure -> {
                    requireContext().showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
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

    private fun setBannerData() {
        setPromotionAdapter()
        bannerAdapter.setBannerCount(viewModel.bannerCount)
        bannerAdapter.submitList(viewModel.bannerData)
        setPromotionIndicator(scrollBinding.vpDiscoverBanner)
        registerPromotionPageCallback(scrollBinding.vpDiscoverBanner)
    }

    private fun setPromotionAdapter() {
        scrollBinding.vpDiscoverBanner.adapter = bannerAdapter
    }

    private fun setPromotionIndicator(vp: ViewPager2) {
        val indicator = scrollBinding.ciDiscoverBanner
        indicator.setViewPager(vp)
        indicator.createIndicators(viewModel.bannerCount, PAGE_NUM / 2)
    }

    private fun registerPromotionPageCallback(vp: ViewPager2) {
        pageRegisterCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                scrollBinding.ciDiscoverBanner.animatePageSelected(position % viewModel.bannerCount)
                currentPosition = position
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    scrollBinding.ciDiscoverBanner.animatePageSelected(position % viewModel.bannerCount)
                }
            }
        }
        vp.registerOnPageChangeCallback(pageRegisterCallback)
    }

    /** 배너 레이아웃 초기화 */

    private fun setPromotionBanner(vp: ViewPager2) {
        setPromotionViewPager(vp)
        setScrollHandler()
        setScrollPageRunnable(vp)
        setTimerTask()
    }

    private fun setPromotionViewPager(vp: ViewPager2) {
        vp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vp.setCurrentItem(PAGE_NUM / 2, false)
    }

    private fun setScrollHandler() {
        scrollHandler = Handler(Looper.getMainLooper())
        timer = Timer()
    }

    private fun setScrollPageRunnable(vp: ViewPager2) {
        scrollPageRunnable = Runnable {
            vp.setCurrentItem(++currentPosition, true)
        }
    }

    private fun setTimerTask() {
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
        scrollBinding.vpDiscoverBanner.unregisterOnPageChangeCallback(pageRegisterCallback)
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

    override fun selectBanner(item: PromotionBanner) {
        if (item.linkUrl.isNotEmpty()) {
            requireContext().showWebBrowser(item.linkUrl)
        }
    }

    companion object {
        private const val PAGE_NUM = 900
        private const val INTERVAL_TIME = 5000L
        private const val SCROLL_DIRECTION = 1
        private const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        private const val EXTRA_ROOT_SCREEN = "rootScreen"
        const val KEY_EDITABLE_DISCOVER_COURSE = "editable_discover_course"
        const val END_PAGE = "HTTP 400 Bad Request"
    }
}