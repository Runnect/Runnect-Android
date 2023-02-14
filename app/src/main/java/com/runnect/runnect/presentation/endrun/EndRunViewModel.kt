package com.runnect.runnect.presentation.endrun

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.model.RequestPostRecordDto
import com.runnect.runnect.data.model.ResponsePostRecordDto
import com.runnect.runnect.data.model.entity.SearchResultEntity
import kotlinx.coroutines.launch

class EndRunViewModel : ViewModel() {

    val service = KApiCourse.ServicePool.courseService //객체 생성

    val distanceSum = MutableLiveData<Double>()
    val captureUri = MutableLiveData<Uri>()
    val departure = MutableLiveData<String>()
    val timerHour = MutableLiveData<String>()
    val timerMinute = MutableLiveData<String>()
    val timerSecond = MutableLiveData<String>()

    val timeTotal = MutableLiveData<String>()
    val paceTotal = MutableLiveData<String>()

    val courseId = MutableLiveData<Int>()
    val publicCourseId = MutableLiveData<Int?>()


    //    "${timerSec} : ${timerMilli}"
    val editTextValue = MutableLiveData<String>()

    val buttonCondition = MutableLiveData<Boolean>()

    val averagePace = MutableLiveData<Int>() //타입?

    val uploadResult = MutableLiveData<ResponsePostRecordDto>()
    val errorMessage = MutableLiveData<String>()

    val currentTime = MutableLiveData<String>() //현재 시간


    val searchResult = MutableLiveData<SearchResultEntity>()

    fun postRecord(request : RequestPostRecordDto) {
        service.also {
            viewModelScope.launch {
                kotlin.runCatching {
                    service.postRecord(RequestPostRecordDto(request.courseId, request.publicCourseId, request.title, request.time, request.pace))
                }.onSuccess {
                    uploadResult.value = it.body()
                }.onFailure {
                    errorMessage.value = it.message
                }
            }
        }

    }
}