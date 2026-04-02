package com.runnect.runnect.presentation.storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.MyDrawCourse
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.domain.entity.PostScrap
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.StorageRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val courseRepository: CourseRepository
) : BaseViewModel() {

    private val _myDrawCourseGetState = MutableLiveData<UiState>(UiState.Empty)
    val myDrawCourseGetState: LiveData<UiState>
        get() = _myDrawCourseGetState

    private var _myDrawCourses = mutableListOf<MyDrawCourse>()
    val myDrawCourses: List<MyDrawCourse>
        get() = _myDrawCourses

    private val _myDrawCourseDeleteState = MutableLiveData<UiState>()
    val myDrawCourseDeleteState: LiveData<UiState>
        get() = _myDrawCourseDeleteState

    private val _myScrapCourseGetState = MutableLiveData<UiStateV2<List<MyScrapCourse>>?>()
    val myScrapCourseGetState: LiveData<UiStateV2<List<MyScrapCourse>>?>
        get() = _myScrapCourseGetState

    private val _courseScrapState = MutableLiveData<UiStateV2<PostScrap>>()
    val courseScrapState: LiveData<UiStateV2<PostScrap>>
        get() = _courseScrapState

    val errorMessage = MutableLiveData<String>()
    val itemSize = MutableLiveData<Int>()
    val myDrawSize = MutableLiveData<Int>()

    var itemsToDeleteLiveData = MutableLiveData<List<Int>>()
    var itemsToDelete: MutableList<Int> = mutableListOf()

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    fun getMyDrawList() = launchWithHandler {
        storageRepository.getMyDrawCourse().onStart {
            _myDrawCourseGetState.value = UiState.Loading
        }.collectResult(
            onSuccess = {
                _myDrawCourses = it.toMutableList()
                _myDrawCourseGetState.value = UiState.Success
            },
            onFailure = {
                errorMessage.value = it.message
                _myDrawCourseGetState.value = UiState.Failure
            }
        )
    }

    fun deleteMyDrawCourse() = launchWithHandler {
        storageRepository.deleteMyDrawCourse(
            RequestPutMyDrawCourse(
                courseIdList = itemsToDelete
            )
        ).onStart {
            _myDrawCourseDeleteState.value = UiState.Loading
        }.collectResult(
            onSuccess = {
                _myDrawCourses = _myDrawCourses.filter {
                    !itemsToDelete.contains(it.courseId)
                }.toMutableList()
                _myDrawCourseDeleteState.value = UiState.Success
            },
            onFailure = {
                _myDrawCourseDeleteState.value = UiState.Failure
            }
        )
    }

    fun getMyScrapCourses() = launchWithHandler {
        storageRepository.getMyScrapCourse().onStart {
            _myScrapCourseGetState.value = UiStateV2.Loading
        }.collectResult(
            onSuccess = {
                _myScrapCourseGetState.value = UiStateV2.Success(it)
                itemSize.value = it.size
            },
            onFailure = {
                Timber.e("${it.message}")
                _myScrapCourseGetState.value = UiStateV2.Failure(it.message.toString())
            }
        )
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) = launchWithHandler {
        val requestPostCourseScrap = RequestPostCourseScrap(
            publicCourseId = id, scrapTF = scrapTF.toString()
        )

        courseRepository.postCourseScrap(requestPostCourseScrap)
            .onStart {
                _courseScrapState.value = UiStateV2.Loading
            }.collectResult(
                onSuccess = {
                    _courseScrapState.value = UiStateV2.Success(it)
                },
                onFailure = {
                    Timber.e(it.toLog())
                    _courseScrapState.value = UiStateV2.Failure(it.toLog())
                }
            )
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

    fun saveClickedCourseId(id: Int) {
        _clickedCourseId = id
    }
}