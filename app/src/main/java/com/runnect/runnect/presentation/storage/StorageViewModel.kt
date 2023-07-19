package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.data.dto.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.RequestPutMyDrawDto
import com.runnect.runnect.domain.StorageRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(private val storageRepository: StorageRepository) :
    ViewModel() {

    private var _myDrawCoursesDeleteState = MutableLiveData<UiState>()
    val myDrawCourseDeleteState: LiveData<UiState>
        get() = _myDrawCoursesDeleteState

    private var _myDrawCourses = mutableListOf<MyDrawCourse>()
    val myDrawCourses: List<MyDrawCourse>
        get() = _myDrawCourses

    val getScrapListResult = MutableLiveData<List<MyScrapCourse>>()

    val errorMessage = MutableLiveData<String>()
    val itemSize = MutableLiveData<Int>()
    val myDrawSize = MutableLiveData<Int>()

    var itemsToDeleteLiveData = MutableLiveData<List<Int>>()

    var itemsToDelete: MutableList<Int> = mutableListOf()

    private var _storageState = MutableLiveData<UiState>(UiState.Empty)
    val storageState: LiveData<UiState>
        get() = _storageState

    fun getMyDrawList() {
        viewModelScope.launch {
            runCatching {
                _storageState.value = UiState.Loading
                storageRepository.getMyDrawCourse()
            }.onSuccess {
                _myDrawCourses = it!!
                Timber.tag(ContentValues.TAG)
                    .d("데이터 수신 완료")
                _storageState.value = UiState.Success
            }.onFailure {
                Timber.tag(ContentValues.TAG)
                    .d("onFailure 메세지 : $it")
                errorMessage.value = it.message
                _storageState.value = UiState.Failure
            }
        }
    }

    fun deleteMyDrawCourse() {
        viewModelScope.launch {
            runCatching {
                _myDrawCoursesDeleteState.value = UiState.Loading
                storageRepository.deleteMyDrawCourse(RequestPutMyDrawDto(itemsToDelete))
            }.onSuccess {
                Timber.tag(ContentValues.TAG)
                    .d("삭제 성공입니다")
                _myDrawCourses =
                    _myDrawCourses.filter { !itemsToDelete.contains(it.courseId) }.toMutableList()
                _myDrawCoursesDeleteState.value = UiState.Success

            }.onFailure {
                Timber.tag(ContentValues.TAG)
                    .d("실패했고 문제는 다음과 같습니다 $it")
                _myDrawCoursesDeleteState.value = UiState.Failure
            }
        }
    }

    fun getScrapList() {
        viewModelScope.launch {
            runCatching {
                _storageState.value = UiState.Loading
                storageRepository.getMyScrapCourse()
            }.onSuccess {
                getScrapListResult.value = it
                Timber.tag(ContentValues.TAG).d("스크랩 리스트 사이즈 : ${it!!.size}")
                _storageState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _storageState.value = UiState.Failure
            }
        }

    }

    fun postCourseScrap(id: Int?, scrapTF: Boolean) {
        viewModelScope.launch {
            runCatching {
                storageRepository.postMyScrapCourse(RequestCourseScrap(id!!, scrapTF.toString()))
            }.onSuccess {
                Timber.d("onSuccess 메세지 : $it")
            }.onFailure {
                Timber.d("onFailure 메세지 : $it")
            }
        }
    }

    fun modifyItemsToDelete(id: Int) {
        if (itemsToDelete.contains(id)) {
            itemsToDelete.remove(id)
        } else {
            itemsToDelete.add(id)
        }
        itemsToDeleteLiveData.value = itemsToDelete
    }

    fun clearItemsToDelete() {
        itemsToDelete.clear()
        itemsToDeleteLiveData.value = itemsToDelete
    }
}