package com.runnect.runnect.presentation.search


import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.data.model.entity.SearchResultEntity
import com.runnect.runnect.databinding.ActivitySearchBinding
import com.runnect.runnect.presentation.departure.DepartureActivity
import com.runnect.runnect.presentation.departure.DepartureActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import com.runnect.runnect.presentation.discover.search.adapter.DiscoverSearchAdapter
import com.runnect.runnect.presentation.draw.DrawActivity
import com.runnect.runnect.presentation.search.adapter.SearchAdapter
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.callback.OnSearchClick
import com.runnect.runnect.util.extension.clearFocus
import com.runnect.runnect.util.extension.setFocusAndShowKeyboard
import timber.log.Timber


class SearchActivity :
    com.runnect.runnect.binding.BindingActivity<ActivitySearchBinding>(R.layout.activity_search),
    OnSearchClick {

    val viewModel: SearchViewModel by viewModels()

    private lateinit var searchAdapter : SearchAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        initDivider()

        binding.etSearch.setFocusAndShowKeyboard(this)
        addListener()
        addObserver()


    }

    private fun initDivider() {
        val dividerItemDecoration = DividerItemDecoration(this,
            LinearLayoutManager(this).orientation)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        binding.recyclerViewSearch.apply {
            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private fun initAdapter() {
        searchAdapter = SearchAdapter(this).apply {
            submitList(viewModel.dataList.value) //다른 옵저버로 뺄까
        }
        binding.recyclerViewSearch.adapter = searchAdapter
    }

    override fun selectItem(item: SearchResultEntity) {
        startActivity(
            Intent(this, DrawActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
                putExtra("searchResult", item)
            }
        )
    }
    
    private fun addObserver() {

        viewModel.searchState.observe(this) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
                UiState.Loading -> {
                    binding.indeterminateBar.isVisible = true
                }
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    if (viewModel.dataList.value.isNullOrEmpty()) {
                        with(binding) {
                            ivNoSearchResult.isVisible = true
                            emptyResultTextView.isVisible = true
                            recyclerViewSearch.isVisible = false //지훈이는 이거 말고 스크롤뷰를 가지고 visible을 처리해줬음

                        }
                    } else {
                        with(binding) {
                            ivNoSearchResult.isVisible = false
                            emptyResultTextView.isVisible = false
                            recyclerViewSearch.isVisible = true

                            initAdapter()

                        }
                    }
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.searchError.value}")
                }
            }
        }


    }


    private fun searchKeyword(keywordString: String) {
        viewModel.getSearchList(keywordString = keywordString)
    }

    private fun addListener() {
        binding.imgBtnBack.setOnClickListener {
            finish()
        }
        //키보드 검색 버튼 클릭 시 이벤트 실행 후 키보드 내리기
        //추후 showToast -> API 호출 대체 예정
        binding.etSearch.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == IME_ACTION_SEARCH) {
                    searchKeyword(binding.etSearch.text.toString())

                    // 키패드 내리기
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

                    return true
                }
                return false
            }
        })
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


}