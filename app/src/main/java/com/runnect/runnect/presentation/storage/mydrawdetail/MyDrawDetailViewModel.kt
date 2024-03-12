package com.runnect.runnect.presentation.storage.mydrawdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetail
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyDrawDetailViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    ViewModel() {
    val myDrawToRunData = MutableLiveData<CourseData>()
    val getResult = MutableLiveData<ResponseGetMyDrawDetail>()
    var isNowUser: Boolean = true
    private val _myDrawCourseDeleteState = MutableLiveData<UiStateV2<Unit>>()
    val myDrawCourseDeleteState: LiveData<UiStateV2<Unit>>
        get() = _myDrawCourseDeleteState

    fun getMyDrawDetail(courseId: Int) {
        viewModelScope.launch {
            runCatching {
                courseRepository.getMyDrawDetail(
                    courseId = courseId
                )
            }.onSuccess { response ->
                val responseBody = response.body()
                if (responseBody == null) {
                    Timber.e("get my draw course response is null")
                    return@launch
                }

                responseBody.let {
                    Timber.d("$responseBody")
                    getResult.value = it
                    isNowUser = it.data.course.isNowUser
                }

            }.onFailure { t ->
                Timber.e("${t.message}")
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
                _myDrawCourseDeleteState.value = UiStateV2.Success(Unit)
            }.onFailure { t ->
                Timber.e("FAIL DELETE MY DRAW COURSE")
                _myDrawCourseDeleteState.value = UiStateV2.Failure(t.message.toString())
            }
        }
    }
}