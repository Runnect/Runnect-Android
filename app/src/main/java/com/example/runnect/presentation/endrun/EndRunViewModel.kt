package com.example.runnect.presentation.endrun

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.runnect.presentation.search.entity.SearchResultEntity
import kotlinx.android.parcel.Parcelize

class EndRunViewModel : ViewModel() {

    val distance = MutableLiveData<Double>()
    val timerSec = MutableLiveData<Int>() //string인가
    val timerMilli = MutableLiveData<Int>()
    val averagePace = MutableLiveData<Int>() //이거 타입 모르겠네



    val searchResult = MutableLiveData<SearchResultEntity>()
}