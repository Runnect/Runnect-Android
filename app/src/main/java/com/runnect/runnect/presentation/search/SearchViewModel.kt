package com.runnect.runnect.presentation.search

import android.content.ContentValues
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiSearch
import com.runnect.runnect.data.model.entity.LocationLatLngEntity
import com.runnect.runnect.data.model.entity.SearchResultEntity
import com.runnect.runnect.data.model.tmap.Poi
import com.runnect.runnect.data.model.tmap.Pois
import com.runnect.runnect.data.model.tmap.SearchResponseTmapDto
import com.runnect.runnect.presentation.state.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel : ViewModel() {

    private val service = KApiSearch.ServicePool.searchService //객체 생성

    val searchResult = MutableLiveData<SearchResponseTmapDto>()
    val searchError = MutableLiveData<String>()

    val dataList = MutableLiveData<List<SearchResultEntity>?>()

    private var _courseInfoState = MutableLiveData<UiState>(UiState.Loading)
    val courseInfoState: LiveData<UiState>
        get() = _courseInfoState


    fun getSearchList(keywordString: String) {
        viewModelScope.launch {
            runCatching {
                _courseInfoState.value = UiState.Loading //밑줄로 내리니까 it으로 못받아와서 위로 올려주었음
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
                _courseInfoState.value = UiState.Success
            }.onFailure {
                searchError.value = it.message
                Timber.tag(ContentValues.TAG).d("Failure : ${it.message}")
                _courseInfoState.value = UiState.Failure
            }
        }
    }

    private fun setData(pois: Pois) {
        dataList.value = pois.poi.map {
            SearchResultEntity(
                fullAdress = makeMainAdress(it),
                name = it.name ?: "",
                locationLatLng = LocationLatLngEntity(it.noorLat, it.noorLon)
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