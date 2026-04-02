package com.runnect.runnect.presentation.storage.mydrawdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.request.RequestPatchMyDrawCourseTitle
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.EditableMyDrawCourseDetail
import com.runnect.runnect.domain.entity.MyDrawCourseDetail
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyDrawDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : BaseViewModel() {

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

    private var _courseTitle = ""
    val courseTitle get() = _courseTitle

    fun getMyDrawDetail(courseId: Int) = launchWithHandler {
        courseRepository.getMyDrawDetail(
            courseId = courseId
        ).collectResult(
            onSuccess = { response ->
                Timber.d("SUCCESS GET MY DRAW COURSE")
                _courseGetState.value = UiStateV2.Success(response)
                _courseTitle = response.title
            },
            onFailure = { t ->
                Timber.e("FAIL GET MY DRAW COURSE: ${t.toLog()}")
                _courseGetState.value = UiStateV2.Failure(t.toLog())
            }
        )
    }

    fun deleteMyDrawCourse(deleteList: MutableList<Int>) = launchWithHandler {
        courseRepository.deleteMyDrawCourse(
            RequestPutMyDrawCourse(deleteList)
        ).collectResult(
            onSuccess = {
                Timber.d("SUCCESS DELETE MY DRAW COURSE")
                _courseDeleteState.value = UiStateV2.Success(Unit)
            },
            onFailure = { t ->
                Timber.e("FAIL DELETE MY DRAW COURSE: ${t.toLog()}")
                _courseDeleteState.value = UiStateV2.Failure(t.toLog())
            }
        )
    }

    fun patchCourseTitle(courseId: Int) = launchWithHandler {
        courseRepository.patchMyDrawCourseTitle(
            courseId,
            RequestPatchMyDrawCourseTitle(courseTitle)
        ).collectResult(
            onSuccess = { response ->
                Timber.d("SUCCESS PATCH MY DRAW COURSE TITLE")
                _coursePatchState.value = UiStateV2.Success(response)
            },
            onFailure = { t ->
                Timber.e("FAIL PATCH MY DRAW COURSE TITLE: ${t.toLog()}")
                _coursePatchState.value = UiStateV2.Failure(t.toLog())
            }
        )
    }

    fun updateCourseTitle(title: String) {
        _courseTitle = title
    }
}