package com.runnect.runnect.presentation.storage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetScrapDto
import kotlinx.coroutines.launch
import timber.log.Timber

class StorageViewModel : ViewModel() {

    val service = KApiCourse.ServicePool.courseService //객체 생성

    val getMyDrawResult = MutableLiveData<ResponseGetCourseDto>()
    val getScrapListResult = MutableLiveData<ResponseGetScrapDto>()
    val errorMessage = MutableLiveData<String>()


    fun getMyDrawList() {

        viewModelScope.launch {
            runCatching {
                service.getCourseList()
            }.onSuccess {
                getMyDrawResult.value = it.body()
            }.onFailure {
                errorMessage.value = it.message
            }
        }
    }

    fun getScrapList() {
        viewModelScope.launch {
            runCatching {
                service.getScrapList()
            }.onSuccess {
                getScrapListResult.value = it.body()
            }.onFailure {
                errorMessage.value = it.message
            }
        }

    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            runCatching {
                service.postCourseScrap(RequestCourseScrap(id, scrapTF.toString()))
            }.onSuccess {
                Timber.d("onSuccess 메세지 : $it")
            }.onFailure {
                Timber.d("onFailure 메세지 : $it")
            }
        }
    }
}