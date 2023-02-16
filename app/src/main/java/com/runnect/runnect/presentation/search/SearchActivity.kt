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
import com.runnect.runnect.databinding.ActivitySearchBinding
import com.runnect.runnect.presentation.departure.DepartureActivity
import com.runnect.runnect.presentation.departure.DepartureActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import com.runnect.runnect.presentation.search.adapter.SearchAdapter
import com.runnect.runnect.util.extension.clearFocus
import com.runnect.runnect.util.extension.setFocusAndShowKeyboard
import timber.log.Timber


class SearchActivity :
    com.runnect.runnect.binding.BindingActivity<ActivitySearchBinding>(R.layout.activity_search) {

    val viewModel: SearchViewModel by viewModels()


    private val searchAdapter by lazy {
        SearchAdapter(searchResultClickListener = {
            startActivity(
                Intent(this, DepartureActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
                    putExtra(SEARCH_RESULT_EXTRA_KEY, it)
                }
            )
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        initDivider()
        val recyclerviewSearch = binding.recyclerViewSearch
        recyclerviewSearch.adapter = searchAdapter

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

    private fun addObserver() {

        viewModel.dataList.observe(this) {
            searchAdapter.submitList(it)
            if (it.isNullOrEmpty()) {
                with(binding) {
                    recyclerViewSearch.isVisible = false
                    ivNoSearchResult.isVisible = true
                    emptyResultTextView.isVisible = true
                }
            } else {
                with(binding) {
                    recyclerViewSearch.isVisible = true
                    ivNoSearchResult.isVisible = false
                    emptyResultTextView.isVisible = false
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