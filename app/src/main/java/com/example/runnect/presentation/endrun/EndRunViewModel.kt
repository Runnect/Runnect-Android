package com.example.runnect.presentation.endrun

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.runnect.presentation.search.entity.SearchResultEntity
import kotlinx.android.parcel.Parcelize

class EndRunViewModel : ViewModel() {

    val searchResult = MutableLiveData<SearchResultEntity>()
}