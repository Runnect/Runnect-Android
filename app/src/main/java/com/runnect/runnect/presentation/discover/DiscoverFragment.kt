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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.data.dto.DiscoverPromotionItemDTO
import com.runnect.runnect.data.model.BannerData
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
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnItemClick
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover),
    OnItemClick, OnHeartClick {
    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var courseRecommendAdapter: CourseRecommendAdapter
    private lateinit var promotionAdapter: DiscoverPromotionAdapter
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var scrollHandler: Handler
    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var scrollPageRunnable: Runnable
    private var currentPosition = PAGE_NUM / 2
    private var root: String = ""

    var isFromStorageScrap = StorageScrapFragment.isFromStorageNoScrap
    var isVisitorMode: Boolean = MainActivity.isVisitorMode
    val db = Firebase.firestore // Cloud Firestore 인스턴스 초기화

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val promotionImages = mutableListOf(
            DiscoverPromotionItemDTO(R.drawable.discover_promotion1),
            DiscoverPromotionItemDTO(R.drawable.discover_promotion2),
            DiscoverPromotionItemDTO(R.drawable.discover_promotion3)
        )
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        initLayout()
        getRecommendCourses()
        addListener()
        addObserver()
        setResultDetail()
        setPromotion(binding.vpDiscoverPromotion, promotionImages)
        getFirebaseData()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isFromStorageScrap) {
                StorageScrapFragment.isFromStorageNoScrap = false
                handleReturnToDiscover()
            } else {
                activity?.onBackPressed()
            }
        }
    }

    // Firestore에서 데이터를 받아오는 함수
    fun getFirebaseData() {
        db.collection("data")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.tag("FirebaseData").d("fail : ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val bannerDataList = mutableListOf<BannerData>()
                    for (document in snapshot) {
                        val imageUrl = document.getString("imageUrl")
                        val linkUrl = document.getString("linkUrl")
                        val bannerData = BannerData(
                            imageUrl = imageUrl.toString(),
                            linkUrl = linkUrl.toString()
                        )
                        bannerDataList.add(
                            bannerData
                        )
                    }
                    Timber.tag("FirebaseData").d("bannerDataList : $bannerDataList")
                }
            }
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
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
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
        setPromotionAdapter(vpList)
        setPromotionIndicator(vp)
        setPromotionViewPager(vp)
        setScrollHandler(vp)
    }

    private fun setPromotionAdapter(vpList: MutableList<DiscoverPromotionItemDTO>) {
        promotionAdapter = DiscoverPromotionAdapter()
        promotionAdapter.submitList(vpList)
        binding.vpDiscoverPromotion.adapter = promotionAdapter
        setPromotionIndicator(binding.vpDiscoverPromotion)
    }

    private fun setPromotionIndicator(vp: ViewPager2) {
        val indicator = binding.ciDiscoverPromotion
        indicator.setViewPager(vp)
        indicator.createIndicators(FRAME_NUM, PAGE_NUM / 2)
    }

    private fun setPromotionViewPager(vp: ViewPager2) {
        vp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vp.setCurrentItem(PAGE_NUM / 2, false)
        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.ciDiscoverPromotion.animatePageSelected(position % FRAME_NUM)
                currentPosition = position
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    binding.ciDiscoverPromotion.animatePageSelected(position % FRAME_NUM)
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
        autoScrollStart()
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

    companion object {
        const val FRAME_NUM = 3
        const val PAGE_NUM = 900
        const val INTERVAL_TIME = 5000L
        const val COURSE_DISCOVER_TAG = "discover"
    }
}