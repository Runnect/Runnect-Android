package com.runnect.runnect.presentation.run

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.runnect.runnect.data.model.entity.LocationLatLngEntity

class RunViewModel : ViewModel() {

    var distanceSum = MutableLiveData(0.0)

    val startLatLng = MutableLiveData<LocationLatLngEntity>()

//    java.lang.IllegalStateException: Cannot invoke setValue on a background thread라고 떠서 일단 숨겨줌
//    var timerSec = MutableLiveData<String>()
//    var timerMilli = MutableLiveData<String>()

    val departure = MutableLiveData<String>()
    val captureUri = MutableLiveData<String>()
}