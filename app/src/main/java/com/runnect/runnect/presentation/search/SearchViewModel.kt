package com.runnect.runnect.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.domain.repository.DepartureSearchRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val departureSearchRepository: DepartureSearchRepository
) : BaseViewModel() {

    val searchError = MutableLiveData<String>()

    val dataList = MutableLiveData<List<SearchResultEntity>?>()

    private var _searchState = MutableLiveData<UiState>(UiState.Empty)
    val searchState: LiveData<UiState>
        get() = _searchState

    fun getSearchList(searchKeyword: String) = launchWithHandler {
        departureSearchRepository.getSearchList(
            keyword = searchKeyword
        ).onStart {
            _searchState.value = UiState.Loading
        }.collectResult(
            onSuccess = {
                dataList.value = it
                _searchState.value = UiState.Success
            },
            onFailure = {
                searchError.value = it.message
                _searchState.value = UiState.Failure
            }
        )
    }
}