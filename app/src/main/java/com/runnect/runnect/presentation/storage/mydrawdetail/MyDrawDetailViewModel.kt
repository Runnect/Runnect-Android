package com.runnect.runnect.presentation.storage.mydrawdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.request.RequestPatchMyDrawCourseTitle
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.domain.entity.EditableMyDrawCourseDetail
import com.runnect.runnect.domain.entity.MyDrawCourseDetail
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyDrawDetailViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    ViewModel() {
    private val _courseGetState = MutableLiveData<UiStateV2<MyDrawCourseDetail>>()
    val courseGetState: LiveData<UiStateV2<MyDrawCourseDetail>>
        get() = _courseGetState

    private val _courseDeleteState = MutableLiveData<UiStateV2<Unit>>()
    val courseDeleteState: LiveData<UiStateV2<Unit>>
        get() = _courseDeleteState

    private val _coursePatchState = MutableLiveData<UiStateV2<EditableMyDrawCourseDetail>>()
    val coursePatchState: LiveData<UiStateV2<EditableMyDrawCourseDetail>>
        get() = _coursePatchState

    val extraDataForRunning = MutableLiveData<CourseData>()

    private val _myDrawCourseDetail = MutableLiveData<MyDrawCourseDetail?>()
    val myDrawCourseDetail: LiveData<MyDrawCourseDetail?>
        get() = _myDrawCourseDetail

    fun getMyDrawDetail(courseId: Int) {
        viewModelScope.launch {
            courseRepository.getMyDrawDetail(
                courseId = courseId
            ).onSuccess { response ->
                if (response == null) {
                    _courseGetState.value = UiStateV2.Failure("MyDrawCourseDetail is null")
                    return@launch
                }

                Timber.d("SUCCESS GET MY DRAW COURSE")
                _courseGetState.value = UiStateV2.Success(response)
                _myDrawCourseDetail.value = response
            }.onFailure { t ->
                Timber.e("FAIL GET MY DRAW COURSE")
                _courseGetState.value = UiStateV2.Failure(t.message.toString())
            }
        }
    }

    fun deleteMyDrawCourse(deleteList: MutableList<Int>) {
        viewModelScope.launch {
            runCatching {
                courseRepository.deleteMyDrawCourse(
                    RequestPutMyDrawCourse(
                        courseIdList = deleteList
                    )
                )
            }.onSuccess { response ->
                Timber.d("SUCCESS DELETE MY DRAW COURSE: ${response.body()}")
                _courseDeleteState.value = UiStateV2.Success(Unit)
            }.onFailure { t ->
                Timber.e("FAIL DELETE MY DRAW COURSE")
                _courseDeleteState.value = UiStateV2.Failure(t.message.toString())
            }
        }
    }

    fun patchCourseTitle(courseId: Int) {
        viewModelScope.launch {
            val course = myDrawCourseDetail.value ?: return@launch
            courseRepository.patchMyDrawCourseTitle(courseId, RequestPatchMyDrawCourseTitle(course.title))
                .onSuccess { response ->
                    if(response == null){
                        _coursePatchState.value = UiStateV2.Failure("PATCH MY DRAW COURSE RESPONSE IS NULL")
                        return@launch
                    }

                    Timber.d("SUCCESS PATCH MY DRAW COURSE TITLE")
                    _coursePatchState.value = UiStateV2.Success(response)
                }
                .onFailure { t ->
                    Timber.e("FAIL PATCH MY DRAW COURSE TITLE")
                    _coursePatchState.value = UiStateV2.Failure(t.message.toString())
                }
        }
    }

    fun updateCourseTitle(title: String) {
        myDrawCourseDetail.value?.title = title
    }
}