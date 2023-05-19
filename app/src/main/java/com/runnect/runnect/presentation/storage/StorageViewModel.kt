package com.runnect.runnect.presentation.storage

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.lifecycle.*
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.model.RequestPutMyDrawDto
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.data.model.ResponsePutMyDrawDto
import com.runnect.runnect.presentation.state.UiState
import kotlinx.coroutines.launch
import timber.log.Timber

class StorageViewModel : ViewModel() {

    val service = KApiCourse.ServicePool.courseService //객체 생성
    val selectList = MutableLiveData<MutableList<Long>>()
    val currentList =  MutableLiveData<MutableList<ResponseGetCourseDto.Data.Course>>()

    val getMyDrawResult = MutableLiveData<ResponseGetCourseDto>()
    val getScrapListResult = MutableLiveData<ResponseGetScrapDto>()
    val errorMessage = MutableLiveData<String>()
    val itemSize = MutableLiveData<Int>()
    val deleteCount = MutableLiveData<Int>()


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
                Timber.tag(ContentValues.TAG)
                    .d("데이터 수신 완료")
                _storageState.value = UiState.Success
            }.onFailure {
                Timber.tag(ContentValues.TAG)
                    .d("문제가 뭐냐 $it")
                errorMessage.value = it.message
                _storageState.value = UiState.Failure
            }
        }
    }

    fun deleteMyDrawCourse(deleteList : MutableList<Long>) {
        viewModelScope.launch {
            runCatching {
//                _storageState.value = UiState.Loading
                service.deleteMyDrawCourse(RequestPutMyDrawDto(deleteList))
            }.onSuccess {
                Timber.tag(ContentValues.TAG)
                    .d("삭제 성공입니다")
//                _storageState.value = UiState.Success
            }.onFailure {
                Timber.tag(ContentValues.TAG)
                    .d("실패했고 문제는 다음과 같습니다 $it")
//                _storageState.value = UiState.Failure
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
                Timber.tag(ContentValues.TAG).d("스크랩 리스트 사이즈 : ${it.body()!!.data.scraps.size}")
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