package com.runnect.runnect.presentation.search

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.model.entity.SearchResultEntity
import com.runnect.runnect.domain.DepartureSearchRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val departureSearchRepository: DepartureSearchRepository) :
    ViewModel() {

    private val _searchKeyword = MutableLiveData<String>()
    val searchKeyword: LiveData<String> get() = _searchKeyword

    fun getSearchKeyword(s: CharSequence, start: Int, before: Int, count: Int) {
        _searchKeyword.value = s.toString()
        Timber.tag(ContentValues.TAG).d("EditText값 : ${_searchKeyword.value}")
    }

    val searchError = MutableLiveData<String>()

    val dataList = MutableLiveData<List<SearchResultEntity>?>()

    private var _searchState = MutableLiveData<UiState>(UiState.Empty)
    val searchState: LiveData<UiState>
        get() = _searchState


    fun getSearchList(searchKeyword: String) {
        viewModelScope.launch {
            runCatching {
                _searchState.value = UiState.Loading
                departureSearchRepository.getSearchList(keyword = searchKeyword)
            }.onSuccess {
                if (it != null) {
                    dataList.value = it
                    Timber.tag(ContentValues.TAG).d("SuccessNotNull : getSearchList body is not null")
                } else {
                    dataList.value = null
                    Timber.tag(ContentValues.TAG).d("SuccessButNull : getSearchList body is null")
                }
                _searchState.value = UiState.Success
            }.onFailure {
                searchError.value = it.message
                _searchState.value = UiState.Failure
            }
        }
    }
}