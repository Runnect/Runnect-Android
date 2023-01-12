package com.example.runnect.presentation.storage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runnect.presentation.storage.api.ApiCourse
import com.example.runnect.presentation.storage.api.dto.ResponseGetCourseDto
import kotlinx.coroutines.launch

class StorageViewModel : ViewModel() {

    val service = ApiCourse.ServicePool.courseService //객체 생성

    val getResult = MutableLiveData<ResponseGetCourseDto>()
    val errorMessage = MutableLiveData<String>()

    fun getCourseList() {

        service.also {
            viewModelScope.launch {
                kotlin.runCatching {
                    service.getCourseList()
                }.onSuccess {
                    getResult.value = it.body()
                }.onFailure {
                    errorMessage.value = it.message
                }
            }
        }

    }
}