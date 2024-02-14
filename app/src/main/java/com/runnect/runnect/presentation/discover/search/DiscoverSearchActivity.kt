package com.runnect.runnect.presentation.discover.search

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityDiscoverSearchBinding
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.discover.DiscoverFragment.Companion.EXTRA_EDITABLE_DISCOVER_COURSE
import com.runnect.runnect.presentation.discover.model.EditableDiscoverCourse
import com.runnect.runnect.presentation.discover.search.adapter.DiscoverSearchAdapter
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_CLICK_TRY_SEARCH_COURSE
import com.runnect.runnect.util.analytics.EventName.VIEW_COURSE_SEARCH
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.getCompatibleParcelableExtra
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showKeyboard
import com.runnect.runnect.util.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverSearchActivity :
    BindingActivity<ActivityDiscoverSearchBinding>(R.layout.activity_discover_search) {
    private val viewModel: DiscoverSearchViewModel by viewModels()
    private lateinit var searchAdapter: DiscoverSearchAdapter

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedCourse: EditableDiscoverCourse =
                    result.data?.getCompatibleParcelableExtra(EXTRA_EDITABLE_DISCOVER_COURSE)
                        ?: return@registerForActivityResult

                searchAdapter.updateSearchItem(viewModel.clickedCourseId, updatedCourse)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this@DiscoverSearchActivity
        Analytics.logClickedItemEvent(VIEW_COURSE_SEARCH)
        showSearchKeyboard()
        initSearchAdapter()
        initSearchRecyclerView()
        registerBackPressedCallback()
        addObserver()
        addListener()
    }

    private fun showSearchKeyboard() {
        binding.etDiscoverSearchTitle.showKeyboard(this)
    }

    private fun initSearchAdapter() {
        searchAdapter = DiscoverSearchAdapter(
            onRecommendItemClick = { courseId ->
                navigateToDetailScreen(courseId)
                viewModel.saveClickedCourseId(courseId)
            },
            onHeartButtonClick = { courseId, scrap ->
                viewModel.postCourseScrap(id = courseId, scrapTF = scrap)
            }
        )
    }

    private fun navigateToDetailScreen(publicCourseId: Int) {
        val intent = Intent(this@DiscoverSearchActivity, CourseDetailActivity::class.java).apply {
            putExtra(EXTRA_PUBLIC_COURSE_ID, publicCourseId)
            putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.COURSE_DISCOVER_SEARCH)
        }
        resultLauncher.launch(intent)
        applyScreenEnterAnimation()
    }

    private fun initSearchRecyclerView() {
        binding.rvDiscoverSearch.apply {
            setHasFixedSize(true)
            adapter = searchAdapter
            layoutManager = GridLayoutManager(this@DiscoverSearchActivity, 2)
            addItemDecoration(
                GridSpacingItemDecoration(
                    context = this@DiscoverSearchActivity,
                    spanCount = 2,
                    horizontalSpaceSize = 6,
                    topSpaceSize = 20
                )
            )
        }
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToPreviousScreenWithAnimation()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun addListener() {
        initBackButtonClickListener()
        initSearchButtonClickListener()
    }

    private fun initBackButtonClickListener() {
        binding.ivDiscoverSearchBack.setOnClickListener {
            navigateToPreviousScreenWithAnimation()
        }
    }

    private fun initSearchButtonClickListener() {
        binding.etDiscoverSearchTitle.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == IME_ACTION_SEARCH) {
                    val keyword = binding.etDiscoverSearchTitle.text
                    if (!keyword.isNullOrBlank()) {
                        Analytics.logClickedItemEvent(EVENT_CLICK_TRY_SEARCH_COURSE)
                        viewModel.getCourseSearch(keyword = keyword.toString())
                        hideKeyboard(binding.etDiscoverSearchTitle)
                    }
                    return true
                }
                return false
            }
        })
    }

    private fun addObserver() {
        setupCourseSearchStateObserver()
        setupCourseScrapStateObserver()
    }

    private fun setupCourseSearchStateObserver() {
        viewModel.courseSearchState.observe(this) { state ->
            when (state) {
                is UiStateV2.Loading -> showProgressBar()

                is UiStateV2.Success -> {
                    dismissProgressBar()
                    showRecyclerView()
                    searchAdapter.submitList(state.data)
                }

                else -> {
                    dismissProgressBar()
                    showEmptySearchResult()
                }
            }
        }
    }

    private fun showRecyclerView() {
        binding.rvDiscoverSearch.isVisible = true
        binding.clDiscoverSearchNoResult.isVisible = false
    }

    private fun showEmptySearchResult() {
        with(binding) {
            rvDiscoverSearch.isVisible = false
            clDiscoverSearchNoResult.isVisible = true
        }
    }

    private fun showProgressBar() {
        binding.pbDiscoverSearch.isVisible = true
    }

    private fun dismissProgressBar() {
        binding.pbDiscoverSearch.isVisible = false
    }

    private fun setupCourseScrapStateObserver() {
        viewModel.courseScrapState.observe(this) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val response = state.data ?: return@observe
                    searchAdapter.updateCourseScrap(
                        publicCourseId = response.publicCourseId.toInt(),
                        scrap = response.scrapTF
                    )
                }

                is UiStateV2.Failure -> {
                    showSnackbar(binding.root, state.msg)
                }

                else -> {}
            }
        }
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
                        hideKeyboard(binding.etDiscoverSearchTitle)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    companion object {
        private const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        private const val EXTRA_ROOT_SCREEN = "rootScreen"
    }
}