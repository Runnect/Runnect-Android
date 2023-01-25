package com.runnect.runnect.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.runnect.runnect.data.model.entity.SearchResultEntity

class SearchViewModel : ViewModel() {

    val searchResult = MutableLiveData<SearchResultEntity>()
}