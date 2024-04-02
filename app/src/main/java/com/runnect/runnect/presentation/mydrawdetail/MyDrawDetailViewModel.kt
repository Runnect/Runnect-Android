package com.runnect.runnect.presentation.mydrawdetail

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetail
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyDrawDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : BaseViewModel() {

    val distance = MutableLiveData<Float>()
    val image = MutableLiveData<Uri>()
    val myDrawToRunData = MutableLiveData<CourseData>()
    val courseId = MutableLiveData<Int>()
    val getResult = MutableLiveData<ResponseGetMyDrawDetail>()
    val errorMessage = MutableLiveData<String>()

    fun getMyDrawDetail(courseId: Int) = launchWithHandler {
        courseRepository.getMyDrawDetail(courseId)
            .collectResult(
                onSuccess = {
                    getResult.value = it
                },
                onFailure = {
                    errorMessage.value = it.toLog()
                }
            )
    }

    fun deleteMyDrawCourse(deleteList: MutableList<Int>) = launchWithHandler {
        courseRepository.deleteMyDrawCourse(
            RequestPutMyDrawCourse(deleteList)
        )
    }
}