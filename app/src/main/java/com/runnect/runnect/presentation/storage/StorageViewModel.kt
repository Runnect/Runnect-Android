package com.runnect.runnect.presentation.storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseScrap
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.presentation.state.UiState
import kotlinx.coroutines.launch
import timber.log.Timber

class StorageViewModel : ViewModel() {

    val service = KApiCourse.ServicePool.courseService //객체 생성

    val getMyDrawResult = MutableLiveData<ResponseGetCourseDto>()
    val getScrapListResult = MutableLiveData<ResponseGetScrapDto>()
    val errorMessage = MutableLiveData<String>()

    private var _storageState = MutableLiveData<UiState>(UiState.Empty)
    val storageState: LiveData<UiState>
        get() = _storageState

    fun getMyDrawList() {

        viewModelScope.launch {
            runCatching {
                _storageState.value = UiState.Loading
                service.getCourseList()
            }.onSuccess {
                getMyDrawResult.value = it.body()
                _storageState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _storageState.value = UiState.Failure
            }
        }
    }

    fun getScrapList() {
        viewModelScope.launch {
            runCatching {
                _storageState.value = UiState.Loading
                service.getScrapList()
            }.onSuccess {
                getScrapListResult.value = it.body()
                _storageState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _storageState.value = UiState.Failure
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