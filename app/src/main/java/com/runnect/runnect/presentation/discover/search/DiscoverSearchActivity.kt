package com.runnect.runnect.presentation.discover.search

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityDiscoverSearchBinding
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.discover.search.adapter.DiscoverSearchAdapter
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.listener.OnHeartButtonClick
import com.runnect.runnect.util.callback.listener.OnRecommendItemClick
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverSearchActivity :
    BindingActivity<ActivityDiscoverSearchBinding>(R.layout.activity_discover_search),
    OnRecommendItemClick, OnHeartButtonClick {
    private val viewModel: DiscoverSearchViewModel by viewModels()
    private lateinit var searchAdapter: DiscoverSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this@DiscoverSearchActivity

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
        searchAdapter = DiscoverSearchAdapter(this, this)
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
                    horizontalSpacing = 6,
                    topSpacing = 20
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
                    viewModel.getCourseSearch(
                        keyword = binding.etDiscoverSearchTitle.text.toString()
                    )
                    hideKeyboard(binding.etDiscoverSearchTitle)
                    return true
                }
                return false
            }
        })
    }

    private fun addObserver() {
        setupCourseSearchStateObserver()
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

    override fun selectItem(publicCourseId: Int) {
        Intent(this@DiscoverSearchActivity, CourseDetailActivity::class.java).apply {
            putExtra(EXTRA_PUBLIC_COURSE_ID, publicCourseId)
            putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.COURSE_DISCOVER_SEARCH)
            startActivity(this)
        }
        applyScreenEnterAnimation()
    }

    override fun scrapCourse(id: Int, scrapTF: Boolean) {
        viewModel.postCourseScrap(id, scrapTF)
    }

    companion object {
        private const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        private const val EXTRA_ROOT_SCREEN = "rootScreen"
    }
}