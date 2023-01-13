package com.example.runnect.presentation.run

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class RunViewModel : ViewModel() {

    var distanceSum = MutableLiveData<Double>(0.0)


}