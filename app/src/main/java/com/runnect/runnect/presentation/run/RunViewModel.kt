package com.runnect.runnect.presentation.run

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng

class RunViewModel : ViewModel() {

    var distanceSum = MutableLiveData(0.0)
    val startLatLng = MutableLiveData<LatLng>()
    val departure = MutableLiveData<String>()
    val captureUri = MutableLiveData<String>()
    var touchList = MutableLiveData<ArrayList<LatLng>>() //ArrayList말고 다른 걸로 할까
    val dataFrom = MutableLiveData<String>()

    var courseId = MutableLiveData<Int>()
    var publicCourseId = MutableLiveData<Int?>()


//    java.lang.IllegalStateException: Cannot invoke setValue on a background thread라고 떠서 일단 숨겨줌
//    var timerSec = MutableLiveData<String>()
//    var timerMilli = MutableLiveData<String>()


}