package com.runnect.runnect.presentation.mydrawdetail

import android.content.ContentValues
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.response.RequestPutMyDrawDto
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetailDto
import com.runnect.runnect.presentation.state.UiState
import kotlinx.coroutines.launch
import timber.log.Timber

class MyDrawDetailViewModel : ViewModel() {

    val distance = MutableLiveData<Float>()
    val image = MutableLiveData<Uri>()
    val myDrawToRunData = MutableLiveData<CourseData>()
    val courseId = MutableLiveData<Int>()


    private var _courseInfoState = MutableLiveData<UiState>(UiState.Loading)
    val courseInfoState: LiveData<UiState>
        get() = _courseInfoState

    val service = KApiCourse.ServicePool.courseService //객체 생성

    val getResult = MutableLiveData<ResponseGetMyDrawDetailDto>()
    val errorMessage = MutableLiveData<String>()

    fun getMyDrawDetail(courseId: Int) {
        viewModelScope.launch {
            runCatching {
                service.getMyDrawDetail(courseId)
            }.onSuccess {
                getResult.value = it.body()
            }.onFailure {
                errorMessage.value = it.message
            }
        }
    }

    fun deleteMyDrawCourse(deleteList: MutableList<Int>) {
        viewModelScope.launch {
            runCatching {
//                _storageState.value = UiState.Loading
                service.deleteMyDrawCourse(RequestPutMyDrawDto(deleteList))
            }.onSuccess {
                Timber.tag(ContentValues.TAG)
                    .d("삭제 성공입니다")
//                _storageState.value = UiState.Success
            }.onFailure {
                Timber.tag(ContentValues.TAG)
                    .d("실패했고 문제는 다음과 같습니다 $it")
//                _storageState.value = UiState.Failure
            }
        }
    }
}