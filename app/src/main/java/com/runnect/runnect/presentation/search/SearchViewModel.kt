package com.runnect.runnect.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiSearch
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    val service = KApiSearch.ServicePool.searchService //객체 생성

    fun getSearchList(keywordString: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                service.getSearchLocation(keyword = keywordString)
            }.onSuccess {
//                uploadResult.value = it.body()
            }.onFailure {
//                errorMessage.value = it.message
            }
        }
    }
}