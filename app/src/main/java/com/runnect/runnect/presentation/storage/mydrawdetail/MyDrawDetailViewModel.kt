package com.runnect.runnect.presentation.storage.mydrawdetail

import android.content.ContentValues
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetail
import com.runnect.runnect.data.dto.response.toMyDrawCourse
import com.runnect.runnect.domain.entity.MyDrawCourse
import com.runnect.runnect.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyDrawDetailViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    ViewModel() {
    val myDrawToRunData = MutableLiveData<CourseData>()
    val getResult = MutableLiveData<ResponseGetMyDrawDetail>()
    val errorMessage = MutableLiveData<String>()

    fun getMyDrawDetail(courseId: Int) {
        viewModelScope.launch {
            runCatching {
                courseRepository.getMyDrawDetail(
                    courseId = courseId
                )
            }.onSuccess { response ->
                val responseBody = response.body()
                if (responseBody == null) {
                    errorMessage.value = "get my draw course response is null"
                    return@launch
                }

                responseBody.let {
                    getResult.value = it
                }
            }.onFailure { t ->
                errorMessage.value = t.message
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
            }.onSuccess {
                Timber.tag(ContentValues.TAG).d("삭제 성공입니다")
            }.onFailure {
                Timber.tag(ContentValues.TAG).d("실패했고 문제는 다음과 같습니다 $it")
            }
        }
    }
}