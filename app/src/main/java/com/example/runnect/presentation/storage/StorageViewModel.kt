package com.example.runnect.presentation.storage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runnect.data.api.KApiCourse
import com.example.runnect.data.model.ResponseGetCourseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
@HiltViewModel
class StorageViewModel : ViewModel() {

    val service = KApiCourse.ServicePool.courseService //객체 생성

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