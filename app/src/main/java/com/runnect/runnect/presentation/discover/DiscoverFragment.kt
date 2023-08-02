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
import androidx.lifecycle.lifecycleScope
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
import com.runnect.runnect.util.CustomToast
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.OnBannerClick
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnItemClick
import com.runnect.runnect.util.extension.startWebView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover),
    OnItemClick, OnHeartClick, OnBannerClick {
    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var courseRecommendAdapter: CourseRecommendAdapter
    private lateinit var promotionAdapter: DiscoverPromotionAdapter
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var scrollHandler: Handler
    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var scrollPageRunnable: Runnable
    private var currentPosition = PAGE_NUM / 2

    var isFromStorageScrap = StorageScrapFragment.isFromStorageNoScrap
    var isVisitorMode: Boolean = MainActivity.isVisitorMode

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        initLayout()
        getBannerData()
        getRecommendCourses()
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
    }

    private fun getBannerData() {
        viewModel.getBannerData()

    }

    private fun handleReturnToDiscover() {
        MainActivity.updateStorageScrap()
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

    fun getRecommendCourses() {
        viewModel.getRecommendCourse()
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
    }

    private fun addListener() {
        binding.ivDiscoverSearch.setOnClickListener {
            startActivity(Intent(requireContext(), DiscoverSearchActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
        binding.btnDiscoverUpload.setOnClickListener {

            if (isVisitorMode) {
                CustomToast.createToast(requireContext(), "러넥트에 가입하면 코스를 업로드할 수 있어요").show()
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
                UiState.Empty -> binding.indeterminateBar.isVisible = false
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    setRecommendCourseAdapter()
                }

                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }

        viewModel.bannerState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> binding.indeterminateBarBanner.isVisible = false
                UiState.Loading -> binding.indeterminateBarBanner.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBarBanner.isVisible = false
                    setPromotion( //submitList만 새로 해주면 되는데 이것 외 여러 코드들도 다시 돌리는 건 비효율적이라 리팩토링이 필요합니다.
                        binding.vpDiscoverPromotion,
                        viewModel.bannerData
                    )
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
                submitList(
                    viewModel.recommendCourseList
                )
            }

        binding.rvDiscoverRecommend.setHasFixedSize(true)
        binding.rvDiscoverRecommend.adapter = courseRecommendAdapter

    }

    private fun setPromotion(vp: ViewPager2, vpList: MutableList<DiscoverPromotionItemDTO>) {
        val bannerCount = vpList.size
        setPromotionAdapter(vpList,bannerCount)
        setPromotionIndicator(vp,bannerCount)
        setPromotionViewPager(vp,bannerCount)
        setScrollHandler(vp)
    }

    private fun setPromotionAdapter(vpList: MutableList<DiscoverPromotionItemDTO>, bannerCount: Int) {
        promotionAdapter = DiscoverPromotionAdapter(requireContext(), this)
        promotionAdapter.submitList(vpList)
        binding.vpDiscoverPromotion.adapter = promotionAdapter
        setPromotionIndicator(binding.vpDiscoverPromotion, bannerCount)
    }

    private fun setPromotionIndicator(vp: ViewPager2, bannerCount: Int) {
        val indicator = binding.ciDiscoverPromotion
        indicator.setViewPager(vp)
        indicator.createIndicators(bannerCount, PAGE_NUM / 2)
    }

    private fun setPromotionViewPager(vp: ViewPager2, bannerCount: Int) {
        vp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vp.setCurrentItem(PAGE_NUM / 2, false)
        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.ciDiscoverPromotion.animatePageSelected(position % bannerCount)
                currentPosition = position
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    binding.ciDiscoverPromotion.animatePageSelected(position % bannerCount)
                }
            }
        }
        )
    }

    private fun setScrollHandler(vp: ViewPager2) {
        scrollHandler = Handler(Looper.getMainLooper())
        scrollPageRunnable = Runnable {
            vp.setCurrentItem(++currentPosition, true)
        }
        timer = Timer()
    }

    private fun autoScrollStart() {
        Timber.tag("lifeCycle").d("onCreate: ${getLifecycle().getCurrentState()}")
        timerTask = object : TimerTask() {
            override fun run() {
                scrollHandler.post(scrollPageRunnable)
            }
        }
        timer.schedule(timerTask, INTERVAL_TIME, INTERVAL_TIME)
    }

    private fun autoScrollStop() {
        timerTask.cancel()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(600)
            autoScrollStart()
        }
    }

    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }


    override fun scrapCourse(id: Int?, scrapTF: Boolean) {
        viewModel.postCourseScrap(id!!, scrapTF)
    }

    private fun setResultDetail() {
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                getRecommendCourses()
            }
        }
    }

    override fun selectItem(publicCourseId: Int) {
        val intent = Intent(requireContext(), CourseDetailActivity::class.java)
        intent.putExtra("publicCourseId", publicCourseId)
        intent.putExtra("root", COURSE_DISCOVER_TAG)
        startForResult.launch(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun selectBanner(item: DiscoverPromotionItemDTO) {
        if (item.linkUrl.isNotEmpty()) {
            requireContext().startWebView(item.linkUrl)
        }
    }

    companion object {
        const val PAGE_NUM = 900
        const val INTERVAL_TIME = 5000L
        const val COURSE_DISCOVER_TAG = "discover"
    }
}