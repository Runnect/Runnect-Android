package com.runnect.runnect.presentation.search

import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.databinding.ActivitySearchBinding
import com.runnect.runnect.presentation.draw.DrawActivity
import com.runnect.runnect.presentation.search.adapter.SearchAdapter
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.callback.listener.OnSearchItemClick
import com.runnect.runnect.util.extension.PermissionUtil
import com.runnect.runnect.util.extension.hideKeyboard
import com.runnect.runnect.util.extension.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchActivity : BindingActivity<ActivitySearchBinding>(R.layout.activity_search),
    OnSearchItemClick {
    val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        initDivider()
        backButton()
        binding.etSearch.showKeyboard(this)
        imgBtnSearch()
        addListener()
        addObserver()
    }

    private fun initDivider() {
        val dividerItemDecoration = DividerItemDecoration(
            this,
            LinearLayoutManager(this).orientation
        )
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        binding.recyclerViewSearch.apply {
            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun backButton() {
        binding.imgBtnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun initAdapter() {
        searchAdapter = SearchAdapter(this).apply {
            submitList(viewModel.dataList.value)
        }
        binding.recyclerViewSearch.adapter = searchAdapter
    }

    override fun selectItem(item: SearchResultEntity) {
        startActivity(
            Intent(this, DrawActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra(EXTRA_SEARCH_RESULT, item) //mode == "searchLocation"
            }
        )
    }

    private fun showEmptyView() {
        with(binding) {
            ivNoSearchResult.isVisible = true
            emptyResultTextView.isVisible = true
            recyclerViewSearch.isVisible = false
        }
    }

    private fun hideEmptyView() {
        with(binding) {
            ivNoSearchResult.isVisible = false
            emptyResultTextView.isVisible = false
            recyclerViewSearch.isVisible = true
        }
    }

    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    private fun showSearchResult() {
        if (viewModel.dataList.value.isNullOrEmpty()) {
            showEmptyView()
        } else {
            hideEmptyView()
            initAdapter()
        }
    }

    private fun showErrorMessage() {
        Timber.tag(ContentValues.TAG)
            .d("Failure : ${viewModel.searchError.value}")
    }

    private fun addObserver() {

        viewModel.searchState.observe(this) {
            when (it) {
                UiState.Empty -> hideLoadingBar()
                UiState.Loading -> showLoadingBar()
                UiState.Success -> {
                    hideLoadingBar()
                    showSearchResult()
                }

                UiState.Failure -> {
                    hideLoadingBar()
                    showErrorMessage()
                }
            }
        }
    }

    private fun searchKeyword(keyword: String) {
        viewModel.getSearchList(keyword)
    }

    private fun imgBtnSearch() {
        binding.imgBtnSearch.setOnClickListener {
            viewModel.getSearchList(binding.etSearch.text.toString())
        }
    }

    private fun addListener() {
        binding.etSearch.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == IME_ACTION_SEARCH) {
                    val keyword = binding.etSearch.text
                    if (!keyword.isNullOrBlank()) {
                        searchKeyword(keyword.toString())
                        hideKeyboard(binding.etSearch)
                    }
                    return true
                }
                return false
            }
        })

        binding.cvStartCurrentLocation.setOnClickListener {
            this.let {
                PermissionUtil.requestLocationPermission(
                    it, { startCurrentLocation() },
                    { showPermissionDeniedToast() }, PermissionUtil.PermissionType.LOCATION
                )
            }
        }

        binding.cvStartCustomLocation.setOnClickListener {
            startCustomLocation()
        }
    }

    private fun showPermissionDeniedToast() {
        Toast.makeText(
            this,
            R.string.location_permission_denied,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun startCurrentLocation() {
        startActivity(
            Intent(this, DrawActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra(
                    EXTRA_SEARCH_RESULT,
                    SearchResultEntity(
                        fullAddress = "",
                        name = "",
                        locationLatLng = null,
                        mode = "currentLocation"
                    )
                )
            }
        )
    }

    private fun startCustomLocation() {
        startActivity(
            Intent(this, DrawActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra(
                    EXTRA_SEARCH_RESULT,
                    SearchResultEntity(
                        fullAddress = "",
                        name = "",
                        locationLatLng = null,
                        mode = "customLocation"
                    )
                )
            }
        )
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
                hideKeyboard(focusView)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        const val EXTRA_SEARCH_RESULT = "searchResult"
    }
}