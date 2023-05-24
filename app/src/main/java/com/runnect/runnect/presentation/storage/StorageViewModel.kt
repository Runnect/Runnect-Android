package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.model.MyDrawCourse
import com.runnect.runnect.data.model.RequestPutMyDrawDto
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.domain.StorageMyDrawRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(private val storageMyDrawRepository: StorageMyDrawRepository) :
    ViewModel() {

    val selectList = MutableLiveData<MutableList<Long>>()
    val currentList = MutableLiveData<MutableList<ResponseGetCourseDto.Data.Course>>()

    val getMyDrawResult = MutableLiveData<List<MyDrawCourse>>()
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
                storageMyDrawRepository.getMyDrawCourseList()
            }.onSuccess {
                getMyDrawResult.value = it
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

    fun deleteMyDrawCourse(deleteList: MutableList<Long>) {
        viewModelScope.launch {
            runCatching {
                service.deleteMyDrawCourse(RequestPutMyDrawDto(deleteList))
            }.onSuccess {
                Timber.tag(ContentValues.TAG)
                    .d("삭제 성공입니다")
            }.onFailure {
                Timber.tag(ContentValues.TAG)
                    .d("실패했고 문제는 다음과 같습니다 $it")
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