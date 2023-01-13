package com.runnect.runnect.presentation.endrun

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.runnect.runnect.data.model.entity.SearchResultEntity

class EndRunViewModel : ViewModel() {

    val distanceSum = MutableLiveData<Double>()
    val captureUri = MutableLiveData<Uri>()
    val departure = MutableLiveData<String>()
    val timerSec = MutableLiveData<String>()
    val timerMilli = MutableLiveData<String>()
    val editTextValue = MutableLiveData<String>()

    val buttonCondition = MutableLiveData<Boolean>()

    val averagePace = MutableLiveData<Int>() //이거 타입 모르겠네


    val searchResult = MutableLiveData<SearchResultEntity>()
}