package com.example.runnect.presentation.prestart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.runnect.presentation.search.entity.SearchResultEntity
import kotlinx.android.parcel.Parcelize

class PreStartViewModel : ViewModel() {

    val searchResult = MutableLiveData<SearchResultEntity>()
}