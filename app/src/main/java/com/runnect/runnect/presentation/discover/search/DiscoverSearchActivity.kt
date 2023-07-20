package com.runnect.runnect.presentation.discover.search

import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityDiscoverSearchBinding
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.discover.search.adapter.DiscoverSearchAdapter
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnItemClick
import com.runnect.runnect.util.extension.clearFocus
import com.runnect.runnect.util.extension.setFocusAndShowKeyboard
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DiscoverSearchActivity :
    BindingActivity<ActivityDiscoverSearchBinding>(R.layout.activity_discover_search),
    OnItemClick, OnHeartClick {
    private val viewModel: DiscoverSearchViewModel by viewModels()
    private lateinit var adapter: DiscoverSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        initLayout()
        addObserver()
        addListener()
    }

    private fun initLayout() {
        binding.svDiscoverSearch.isVisible = true
        binding.constDiscoverSearchNoResult.isVisible = false
        //키보드 자동 올리기 - manifest 설정과 확장함수를 동시에 해줘야 동작함.
        binding.etDiscoverSearchTitle.setFocusAndShowKeyboard(this)
        binding.rvDiscoverSearch.apply {
            layoutManager = GridLayoutManager(this@DiscoverSearchActivity, 2)
            addItemDecoration(GridSpacingItemDecoration(this@DiscoverSearchActivity, 2, 6, 16))
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun addListener() {
        binding.ivDiscoverSearchBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        //키보드 검색 버튼 클릭 시 이벤트 실행 후 키보드 내리기
        //추후 showToast -> API 호출 대체 예정
        binding.etDiscoverSearchTitle.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == IME_ACTION_SEARCH) {
                    viewModel.getCourseSearch(
                        keyword = binding.etDiscoverSearchTitle.text.toString()
                    )
                    clearFocus(binding.etDiscoverSearchTitle)
                    return true
                }
                return false
            }
        })
    }

    private fun addObserver() {
        viewModel.courseSearchState.observe(this) {
            Timber.d("${viewModel.courseSearchList.isEmpty()}")
            when (it) {
                UiState.Empty -> {
                    handleUnsuccessfulCourseSearch()
                }

                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    binding.svDiscoverSearch.isVisible = true
                    binding.constDiscoverSearchNoResult.isVisible = false
                    initAdapter()
                }

                UiState.Failure -> {
                    handleUnsuccessfulCourseSearch()
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }

            }
        }

    }

    private fun handleUnsuccessfulCourseSearch() {
        with(binding) {
            indeterminateBar.isVisible = false
            svDiscoverSearch.isVisible = false
            constDiscoverSearchNoResult.isVisible = true
        }
    }


    private fun initAdapter() {
        adapter = DiscoverSearchAdapter(this, this, this).apply {
            submitList(viewModel.courseSearchList)
        }
        binding.rvDiscoverSearch.adapter = this@DiscoverSearchActivity.adapter
    }


    //키보드 밖 터치 시, 키보드 내림
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev!!.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                clearFocus(focusView)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //코스 클릭 시 상세페이지로 이동
    override fun selectItem(publicCourseId: Int) {
        val intent = Intent(this, CourseDetailActivity::class.java)
        intent.putExtra("publicCourseId", publicCourseId)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun scrapCourse(id: Int?, scrapTF: Boolean) {
        viewModel.postCourseScrap(id!!, scrapTF)
    }
}