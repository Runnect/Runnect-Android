package com.runnect.runnect.presentation.mydrawdetail

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetMyDrawDetailDto
import kotlinx.coroutines.launch

class MyDrawDetailViewModel : ViewModel() {

    val distance = MutableLiveData<Double>(0.0)
    val image = MutableLiveData<Uri>()

    val courseId = MutableLiveData<Int>()

    val service = KApiCourse.ServicePool.courseService //객체 생성

    val getResult = MutableLiveData<ResponseGetMyDrawDetailDto>()
    val errorMessage = MutableLiveData<String>()

    fun getMyDrawDetail(courseId : Int) {

        service.also {
            viewModelScope.launch {
                kotlin.runCatching {
                    service.getMyDrawDetail(courseId)
                }.onSuccess {
                    getResult.value = it.body()
                }.onFailure {
                    errorMessage.value = it.message
                }
            }
        }

    }
}