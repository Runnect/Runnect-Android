package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.MyDrawCourse
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponsePostScrap
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.StorageRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {
    private val _myDrawCoursesDeleteState = MutableLiveData<UiState>()
    val myDrawCourseDeleteState: LiveData<UiState>
        get() = _myDrawCoursesDeleteState

    private val _myDrawCoursesGetState = MutableLiveData<UiState>(UiState.Empty)
    val myDrawCoursesGetState: LiveData<UiState>
        get() = _myDrawCoursesGetState

    private var _myDrawCourses = mutableListOf<MyDrawCourse>()
    val myDrawCourses: List<MyDrawCourse>
        get() = _myDrawCourses

    private val _myScrapCoursesGetState = MutableLiveData<UiStateV2<List<MyScrapCourse>>?>()
    val myScrapCoursesGetState: LiveData<UiStateV2<List<MyScrapCourse>>?>
        get() = _myScrapCoursesGetState

    private val _courseScrapState = MutableLiveData<UiStateV2<ResponsePostScrap>>()
    val courseScrapState: LiveData<UiStateV2<ResponsePostScrap>>
        get() = _courseScrapState

    val getScrapListResult = MutableLiveData<List<MyScrapCourse>>()
    val errorMessage = MutableLiveData<String>()

    val itemSize = MutableLiveData<Int>()
    val myDrawSize = MutableLiveData<Int>()

    var itemsToDeleteLiveData = MutableLiveData<List<Int>>()
    var itemsToDelete: MutableList<Int> = mutableListOf()

    fun getMyDrawList() {
        viewModelScope.launch {
            runCatching {
                _myDrawCoursesGetState.value = UiState.Loading
                storageRepository.getMyDrawCourse()
            }.onSuccess {
                _myDrawCourses = it!!
                Timber.tag(ContentValues.TAG).d("데이터 수신 완료")
                _myDrawCoursesGetState.value = UiState.Success
            }.onFailure {
                Timber.tag(ContentValues.TAG).d("onFailure 메세지 : $it")
                errorMessage.value = it.message
                _myDrawCoursesGetState.value = UiState.Failure
            }
        }
    }

    fun deleteMyDrawCourse() {
        viewModelScope.launch {
            runCatching {
                _myDrawCoursesDeleteState.value = UiState.Loading
                storageRepository.deleteMyDrawCourse(
                    RequestPutMyDrawCourse(
                        courseIdList = itemsToDelete
                    )
                )
            }.onSuccess {
                Timber.tag(ContentValues.TAG).d("삭제 성공입니다")
                _myDrawCourses =
                    _myDrawCourses.filter { !itemsToDelete.contains(it.courseId) }.toMutableList()
                _myDrawCoursesDeleteState.value = UiState.Success

            }.onFailure {
                Timber.tag(ContentValues.TAG).d("실패했고 문제는 다음과 같습니다 $it")
                _myDrawCoursesDeleteState.value = UiState.Failure
            }
        }
    }

    fun getMyScrapCoures() {
        viewModelScope.launch {
            _myScrapCoursesGetState.value = UiStateV2.Loading

            storageRepository.getMyScrapCourse()
                .onSuccess {  response ->
                    if (response == null) {
                        _myScrapCoursesGetState.value =
                            UiStateV2.Failure("MY SCRAP COURSE DATA IS NULL")
                        return@launch
                    }

                    Timber.d("MY SCRAP COURSE GET SUCCESS")
                    _myScrapCoursesGetState.value = UiStateV2.Success(response)
                }
                .onFailure { t ->
                    Timber.e("MY SCRAP COURSE GET FAIL")
                    Timber.e("${t.message}")
                    _myScrapCoursesGetState.value = UiStateV2.Failure(t.message.toString())
                }
        }
    }

    // todo: id를 non-null 타입으로 바꾸기
    fun postCourseScrap(id: Int?, scrapTF: Boolean) {
        viewModelScope.launch {
            courseRepository.postCourseScrap(
                RequestPostCourseScrap(
                    publicCourseId = id!!, scrapTF = scrapTF.toString()
                )
            )
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