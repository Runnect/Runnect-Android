package com.runnect.runnect.presentation.run

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RunViewModel : ViewModel() {

    var distanceSum = MutableLiveData(0.0)
    val departure = MutableLiveData<String>()
    val dataFrom = MutableLiveData<String>()

    var courseId = MutableLiveData<Int>()
    var publicCourseId = MutableLiveData<Int?>()
}