package com.runnect.runnect.presentation.mydrawdetail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.model.MyDrawToRunData
import com.runnect.runnect.data.model.ResponseGetMyDrawDetailDto
import com.runnect.runnect.presentation.state.UiState
import kotlinx.coroutines.launch

class MyDrawDetailViewModel : ViewModel() {

    val distance = MutableLiveData<Float>()
    val image = MutableLiveData<Uri>()
    val myDrawToRunData = MutableLiveData<MyDrawToRunData>()
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
}