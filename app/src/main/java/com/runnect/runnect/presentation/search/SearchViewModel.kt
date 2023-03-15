package com.runnect.runnect.presentation.search

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.data.api.KApiSearch
import com.runnect.runnect.data.model.entity.SearchResultEntity
import com.runnect.runnect.data.model.tmap.Poi
import com.runnect.runnect.data.model.tmap.Pois
import com.runnect.runnect.presentation.state.UiState
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel : ViewModel() {

    private val _searchKeyword = MutableLiveData<String>()
    val searchKeyword: LiveData<String> get() = _searchKeyword

    fun getSearchKeyword(s: CharSequence, start: Int, before: Int, count: Int) {
        _searchKeyword.value = s.toString()
        Timber.tag(ContentValues.TAG).d("EditText값 : ${_searchKeyword.value}")
    }


    private val service = KApiSearch.ServicePool.searchService //객체 생성

    val searchError = MutableLiveData<String>()

    val dataList = MutableLiveData<List<SearchResultEntity>?>()

    private var _searchState = MutableLiveData<UiState>(UiState.Empty)
    val searchState: LiveData<UiState>
        get() = _searchState


    fun getSearchList(keywordString: String) {
        viewModelScope.launch {
            runCatching {
                _searchState.value = UiState.Loading
                service.getSearchLocation(keyword = keywordString)
            }.onSuccess {
                if (it.body() != null) {
                    it.body().let { searchResponseSchema ->
                        setData(searchResponseSchema!!.searchPoiInfo.pois)
                    }
                    Timber.tag(ContentValues.TAG).d("Success : getSearchList body is not null")
                } else {
                    dataList.value = null
                    Timber.tag(ContentValues.TAG).d("Success : getSearchList body is null")
                }
                _searchState.value = UiState.Success
            }.onFailure {
                searchError.value = it.message
                _searchState.value = UiState.Failure
            }
        }
    }

    private fun setData(pois: Pois) {
        dataList.value = pois.poi.map {
            SearchResultEntity(
                fullAdress = makeMainAdress(it),
                name = it.name ?: "",
                locationLatLng = LatLng(it.noorLat.toDouble(), it.noorLon.toDouble())
            )
        }
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
}