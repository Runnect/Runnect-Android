package com.example.runnect.presentation.search

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.data.api.KApiSearch
import com.example.runnect.data.model.entity.LocationLatLngEntity
import com.example.runnect.data.model.entity.SearchResultEntity
import com.example.runnect.data.model.tmap.Poi
import com.example.runnect.data.model.tmap.Pois
import com.example.runnect.databinding.ActivitySearchBinding
import com.example.runnect.presentation.departure.DepartureActivity
import com.example.runnect.presentation.departure.DepartureActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import com.example.runnect.presentation.departure.DepartureViewModel
import com.example.runnect.presentation.search.adapter.SearchAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@AndroidEntryPoint
class SearchActivity : BindingActivity<ActivitySearchBinding>(R.layout.activity_search) {

    val viewModel: DepartureViewModel by viewModels()

    private val getSearchService = KApiSearch.ServicePool.searchService

    private val searchAdapter by lazy {
        SearchAdapter(searchResultClickListener = {
            startActivity(
                Intent(this, DepartureActivity::class.java).apply {
                    putExtra(SEARCH_RESULT_EXTRA_KEY, it)
                }
            )
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this


        val recyclerviewSearch = binding.recyclerViewSearch //xml에 짜놓은 리사이클러뷰 불러오고
        recyclerviewSearch.adapter = searchAdapter //위에서 생성한 SearchAdapter 객체랑 연결함

        addListener()


    }


    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }


    private fun setData(pois: Pois) {
        val dataList = pois.poi.map {
            SearchResultEntity(
                fullAdress = makeMainAdress(it),
                name = it.name ?: "",
                locationLatLng = LocationLatLngEntity(it.noorLat, it.noorLon)
            )
        }

        searchAdapter.submitList(dataList)
    }

    private fun searchKeyword(keywordString: String) {


        getSearchService.also {
            CoroutineScope(Dispatchers.Main).launch {
                runCatching { it.getSearchLocation(keyword = keywordString) } //레트로핏은 자동으로 I/O 스레드에서 돌아가는 걸로 앎
                    .onSuccess {
                        if (it.body() != null) {
                            binding.recyclerViewSearch.isVisible = true
                            binding.ivNoSearchResult.isVisible = false
                            binding.emptyResultTextView.isVisible = false


                            it.body()?.let { searchResponseSchema ->
                                setData(searchResponseSchema.searchPoiInfo.pois) //setData()는 맵핑함수
                            }
                        } else {
                            binding.recyclerViewSearch.isVisible = false
                            binding.ivNoSearchResult.isVisible = true
                            binding.emptyResultTextView.isVisible = true
                        }

                        Log.d(ContentValues.TAG, "success")


                    }
                    .onFailure {
                        Log.d(ContentValues.TAG, "fail")
                        Log.d(ContentValues.TAG, "${it.message}")

                    }
            }
        }

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

    //키보드 내리기(포커스 해제) 확장함수
    fun Context.clearFocus(view: View) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }


}