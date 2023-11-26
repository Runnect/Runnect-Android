package com.runnect.runnect.presentation.discover

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.domain.entity.DiscoverPromotionBanner
import com.runnect.runnect.databinding.FragmentDiscoverBinding
import com.runnect.runnect.domain.entity.RecommendCourse
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.discover.adapter.DiscoverRecommendAdapter
import com.runnect.runnect.presentation.discover.adapter.BannerAdapter
import com.runnect.runnect.presentation.discover.pick.DiscoverPickActivity
import com.runnect.runnect.presentation.discover.search.DiscoverSearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.presentation.storage.StorageScrapFragment
import com.runnect.runnect.util.custom.toast.RunnectToast
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.listener.OnBannerItemClick
import com.runnect.runnect.util.callback.listener.OnHeartButtonClick
import com.runnect.runnect.util.callback.listener.OnRecommendItemClick
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showWebBrowser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover),
    OnRecommendItemClick, OnHeartButtonClick, OnBannerItemClick {
    private val viewModel: DiscoverViewModel by viewModels()

    private val bannerAdapter: BannerAdapter by lazy { BannerAdapter(this) }
    private lateinit var recommendAdapter: DiscoverRecommendAdapter

    // 프로모션 배너 관련 변수들
    private lateinit var scrollHandler: Handler
    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var scrollPageRunnable: Runnable
    private lateinit var pageRegisterCallback: ViewPager2.OnPageChangeCallback
    private var currentPosition = PAGE_NUM / 2

    private var isFromStorageScrap = StorageScrapFragment.isFromStorageScrap
    private var isVisitorMode: Boolean = MainActivity.isVisitorMode

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initLayout()
        addListener()
        addObserver()
        registerBackPressedCallback()
        initRefreshLayoutListener()
        initRecommendCourseRecyclerView()
    }

    fun getRecommendCourses(pageNo: String) {
        viewModel.getRecommendCourses(pageNo = pageNo, ordering = "date")
    }

    private fun initRecommendCourseRecyclerView() {
        recommendAdapter = DiscoverRecommendAdapter(
            onHeartButtonClick = this,
            onRecommendItemClick = this,
            isVisitorMode = isVisitorMode
        )

        binding.rvDiscoverRecommend.apply {
            setHasFixedSize(true)
            adapter = recommendAdapter
        }
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
        binding.rvDiscoverRecommend.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDiscoverRecommend.addItemDecoration(
            GridSpacingItemDecoration(
                context = requireContext(),
                spanCount = 2,
                horizontalSpacing = 6,
                topSpacing = 20
            )
        )
        setPromotionBanner(binding.vpDiscoverPromotion)
    }

    private fun addListener() {
        initScrollChangedListener()
        initCourseSearchButtonClickListener()
        initCourseUploadButtonClickListener()
    }

    private fun initScrollChangedListener() {
        binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val contentView = binding.nestedScrollView.getChildAt(0)
            if (contentView != null && binding.nestedScrollView.height + scrollY >= contentView.height) {
                var currentPageNo: Int = viewModel.currentPageNo.value!!.toInt()
                currentPageNo++
                getRecommendCourses(pageNo = currentPageNo.toString())
            }
        }
    }

    private fun initCourseSearchButtonClickListener() {
        binding.ivDiscoverSearch.setOnClickListener {
            startActivity(Intent(requireContext(), DiscoverSearchActivity::class.java))
            requireActivity().applyScreenEnterAnimation()
        }
    }

    private fun initCourseUploadButtonClickListener() {
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
            getRecommendCourses(pageNo = "1")
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun addObserver() {
        setupBannerGetStateObserver()
        setupCourseGetStateObserver()
        setupCourseScrapStateObserver()
    }

    private fun setupBannerGetStateObserver() {
        viewModel.bannerGetState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> binding.indeterminateBarBanner.isVisible = false
                UiState.Loading -> binding.indeterminateBarBanner.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBarBanner.isVisible = false
                    setBannerData()
                }

                UiState.Failure -> {
                    binding.indeterminateBarBanner.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }
    }

    private fun setupCourseGetStateObserver() {
        viewModel.courseGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Loading -> showShimmerLayout()

                is UiStateV2.Success -> {
                    dismissShimmerLayout()

                    if (state.data == null) return@observe
                    recommendAdapter.submitList(state.data)
                }

                is UiStateV2.Failure -> {
                    dismissShimmerLayout()
                    requireContext().showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
    }

    private fun showShimmerLayout() {
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.isVisible = true
    }

    private fun dismissShimmerLayout() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.isVisible = false
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
        setPromotionIndicator(binding.vpDiscoverPromotion)
        registerPromotionPageCallback(binding.vpDiscoverPromotion)
    }

    private fun setPromotionAdapter() {
        binding.vpDiscoverPromotion.adapter = bannerAdapter
    }

    private fun setPromotionIndicator(vp: ViewPager2) {
        val indicator = binding.ciDiscoverPromotion
        indicator.setViewPager(vp)
        indicator.createIndicators(viewModel.bannerCount, PAGE_NUM / 2)
    }

    private fun registerPromotionPageCallback(vp: ViewPager2) {
        pageRegisterCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.ciDiscoverPromotion.animatePageSelected(position % viewModel.bannerCount)
                currentPosition = position
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    binding.ciDiscoverPromotion.animatePageSelected(position % viewModel.bannerCount)
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
        binding.vpDiscoverPromotion.unregisterOnPageChangeCallback(pageRegisterCallback)
    }

    override fun scrapCourse(id: Int?, scrapTF: Boolean) {
        viewModel.postCourseScrap(id!!, scrapTF)
    }

    override fun selectItem(publicCourseId: Int) {
        Intent(requireContext(), CourseDetailActivity::class.java).apply {
            putExtra(EXTRA_PUBLIC_COURSE_ID, publicCourseId)
            putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.COURSE_DISCOVER)
            startActivity(this)
        }
        requireActivity().applyScreenEnterAnimation()
    }

    override fun selectBanner(item: DiscoverPromotionBanner) {
        if (item.linkUrl.isNotEmpty()) {
            requireContext().showWebBrowser(item.linkUrl)
        }
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
        const val PAGE_NUM = 900
        const val INTERVAL_TIME = 5000L
        const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        const val EXTRA_ROOT_SCREEN = "rootScreen"
        const val END_PAGE = "HTTP 400 Bad Request"
    }
}