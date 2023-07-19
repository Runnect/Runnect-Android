package com.runnect.runnect.presentation.run

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng

class RunViewModel : ViewModel() {

    var distanceSum = MutableLiveData(0.0)
    val startLatLng = MutableLiveData<LatLng>()
    val departure = MutableLiveData<String>()
    val captureUri = MutableLiveData<String>()
    var touchList = MutableLiveData<ArrayList<LatLng>>()
    val dataFrom = MutableLiveData<String>()

    var courseId = MutableLiveData<Int>()
    var publicCourseId = MutableLiveData<Int?>()
}