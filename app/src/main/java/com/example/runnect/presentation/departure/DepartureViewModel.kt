package com.example.runnect.presentation.departure

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.runnect.data.model.entity.SearchResultEntity

class DepartureViewModel : ViewModel() {

    val searchResult = MutableLiveData<SearchResultEntity>()
}