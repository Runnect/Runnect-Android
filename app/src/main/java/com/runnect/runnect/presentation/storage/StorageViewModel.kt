package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.model.MyDrawCourse
import com.runnect.runnect.data.model.MyScrapCourse
import com.runnect.runnect.data.model.RequestPutMyDrawDto
import com.runnect.runnect.data.model.ResponseGetCourseDto
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

    val courseCount = MutableLiveData<Int>()

    val errorMessage = MutableLiveData<String>()
    val itemSize = MutableLiveData<Int>()

    private var _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean>
        get() = _editMode

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
                Timber.tag(ContentValues.TAG)
                    .d("갱신전 myDrawCourses값 : ${myDrawCourses.map { it.courseId }}")
                _storageState.value = UiState.Success
            }.onFailure {
                Timber.tag(ContentValues.TAG)
                    .d("문제가 뭐냐 $it")
                errorMessage.value = it.message
                _storageState.value = UiState.Failure
            }
        }
    }

    fun convertMode() {
        _editMode.value = !_editMode.value!!
    }

    fun deleteMyDrawCourse() {
        viewModelScope.launch {
            runCatching {
                _myDrawCoursesDeleteState.value = UiState.Loading
                storageRepository.deleteMyDrawCourse(RequestPutMyDrawDto(itemsToDelete))
            }.onSuccess {
                Timber.tag(ContentValues.TAG)
                    .d("삭제 성공입니다")
                _myDrawCourses = _myDrawCourses.filter { !itemsToDelete.contains(it.courseId) }.toMutableList()
                _myDrawCoursesDeleteState.value = UiState.Success
                //모든 기록 삭제 시, 편집 모드 -> 읽기 모드
                if (_myDrawCourses.isEmpty()) {
                    convertMode()
                }

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

    fun getCourseCount(): String {
        Timber.d("총 기록 ${_myDrawCourses.count()}개")
        courseCount.value = _myDrawCourses.count()
        return "총 기록 ${courseCount.value}개"
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