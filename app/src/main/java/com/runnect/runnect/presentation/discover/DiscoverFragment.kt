package com.runnect.runnect.presentation.discover

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.data.dto.DiscoverPromotionItemDTO
import com.runnect.runnect.databinding.FragmentDiscoverBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.discover.adapter.CourseRecommendAdapter
import com.runnect.runnect.presentation.discover.adapter.DiscoverPromotionAdapter
import com.runnect.runnect.presentation.discover.load.DiscoverLoadActivity
import com.runnect.runnect.presentation.discover.search.DiscoverSearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.storage.StorageScrapFragment
import com.runnect.runnect.util.custom.RunnectToast
import com.runnect.runnect.util.custom.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.OnBannerClick
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnItemClick
import com.runnect.runnect.util.extension.showWebBrowser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover),
    OnItemClick, OnHeartClick, OnBannerClick {
    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var courseRecommendAdapter: CourseRecommendAdapter
    private val promotionAdapter: DiscoverPromotionAdapter by lazy {
        DiscoverPromotionAdapter(requireContext(), this)
    }
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var scrollHandler: Handler
    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var scrollPageRunnable: Runnable
    private lateinit var pageRegisterCallback:ViewPager2.OnPageChangeCallback
    private var currentPosition = PAGE_NUM / 2

    var isFromStorageScrap = StorageScrapFragment.isFromStorageNoScrap
    var isVisitorMode: Boolean = MainActivity.isVisitorMode

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        initLayout()
        getBannerData()
        getRecommendCourses(pageNo = "1")
        addListener()
        addObserver()
        setResultDetail()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isFromStorageScrap) {
                StorageScrapFragment.isFromStorageNoScrap = false
                handleReturnToDiscover()
            } else {
                activity?.onBackPressed()
            }
        }
        pullToRefresh()
    }

    private fun initScrollListener() {
        binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val contentView = binding.nestedScrollView.getChildAt(0)
            if (contentView != null && binding.nestedScrollView.height + scrollY >= contentView.height) {
                var currentPageNo: Int = viewModel.currentPageNo.value!!.toInt()
                currentPageNo++
                getRecommendCourses(pageNo = currentPageNo.toString())
            }
        }
    }

    private fun pullToRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            viewModel.recommendCourseList.clear() //새로고침 시 다시 pageNo가 1부터 시작되는데 기존에 끝까지 받아온 거에 addAll로 계속 누적돼서 clear()로 비워주는 것.
            getRecommendCourses(pageNo = "1")
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun getBannerData() {
        viewModel.getBannerData()

    }

    private fun handleReturnToDiscover() {
        MainActivity.updateStorageScrapScreen()
        requireActivity().finish()
        requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

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

    fun getRecommendCourses(pageNo: String?) {
        viewModel.getRecommendCourse(pageNo = pageNo)
    }

    private fun initLayout() {
        binding.rvDiscoverRecommend.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDiscoverRecommend.addItemDecoration(
            GridSpacingItemDecoration(
                requireContext(),
                2,
                6,
                16
            )
        )
        setPromotionBanner(binding.vpDiscoverPromotion)
    }

    private fun addListener() {
        initScrollListener()

        binding.ivDiscoverSearch.setOnClickListener {
            startActivity(Intent(requireContext(), DiscoverSearchActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
        binding.btnDiscoverUpload.setOnClickListener {

            if (isVisitorMode) {
                RunnectToast.createToast(requireContext(), VISITOR_REQUIRE_LOGIN).show()
            } else {
                startActivity(Intent(requireContext(), DiscoverLoadActivity::class.java))
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
        }
    }

    private fun addObserver() {
        viewModel.courseInfoState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> {}
                UiState.Loading -> {
                    binding.shimmerLayout.startShimmer()
                    binding.shimmerLayout.isVisible = true
                }

                UiState.Success -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.isVisible = false
                    setRecommendCourseAdapter()
                }

                UiState.Failure -> {
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                    if (viewModel.errorMessage.value == END_PAGE) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.isVisible = false
                    }
                }
            }
        }

        viewModel.bannerState.observe(viewLifecycleOwner) {
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


    private fun setRecommendCourseAdapter() {
        courseRecommendAdapter =
            CourseRecommendAdapter(requireContext(), this, this, isVisitorMode).apply {
                submitList(viewModel.recommendCourseList)
            }
        binding.rvDiscoverRecommend.setHasFixedSize(true)
        binding.rvDiscoverRecommend.adapter = courseRecommendAdapter
    }

    private fun setPromotionBanner(vp: ViewPager2) {
        setPromotionViewPager(vp)
        setScrollHandler()
        setScrollPageRunnable(vp)
        setTimerTask()
    }

    private fun setPromotionAdapter() {
        binding.vpDiscoverPromotion.adapter = promotionAdapter
    }

    private fun setBannerData() {
        setPromotionAdapter()
        promotionAdapter.setBannerCount(viewModel.bannerCount)
        promotionAdapter.submitList(viewModel.bannerData)
        setPromotionIndicator(binding.vpDiscoverPromotion)
        registerPromotionPageCallback(binding.vpDiscoverPromotion)
    }

    private fun setPromotionIndicator(vp: ViewPager2) {
        val indicator = binding.ciDiscoverPromotion
        indicator.setViewPager(vp)
        indicator.createIndicators(viewModel.bannerCount, PAGE_NUM / 2)
    }

    private fun setPromotionViewPager(vp: ViewPager2) {
        vp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vp.setCurrentItem(PAGE_NUM / 2, false)
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

    private fun setResultDetail() {
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                getRecommendCourses(pageNo = "1")
            }
        }
    }

    override fun selectItem(publicCourseId: Int) {
        val intent = Intent(requireContext(), CourseDetailActivity::class.java)
        intent.putExtra(EXTRA_PUBLIC_COURSE_ID, publicCourseId)
        intent.putExtra(EXTRA_ROOT, COURSE_DISCOVER_TAG)
        startForResult.launch(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun selectBanner(item: DiscoverPromotionItemDTO) {
        if (item.linkUrl.isNotEmpty()) {
            requireContext().showWebBrowser(item.linkUrl)
        }
    }

    companion object {
        const val PAGE_NUM = 900
        const val INTERVAL_TIME = 5000L
        const val COURSE_DISCOVER_TAG = "discover"
        const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        const val EXTRA_ROOT = "root"
        const val END_PAGE = "HTTP 400 Bad Request"
        const val VISITOR_REQUIRE_LOGIN = "러넥트에 가입하면 코스를 업로드할 수 있어요"
    }
}