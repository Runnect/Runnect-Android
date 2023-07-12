package com.runnect.runnect.presentation.mydrawdetail

import android.content.ContentValues
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.model.MyDrawToRunData
import com.runnect.runnect.data.model.RequestPutMyDrawDto
import com.runnect.runnect.data.model.ResponseGetMyDrawDetailDto
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyDrawDetailViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    ViewModel() {

    val distance = MutableLiveData<Float>()
    val image = MutableLiveData<Uri>()
    val myDrawToRunData = MutableLiveData<MyDrawToRunData>()
    val courseId = MutableLiveData<Int>()


    private var _courseInfoState = MutableLiveData<UiState>(UiState.Loading)
    val courseInfoState: LiveData<UiState>
        get() = _courseInfoState

    val getResult = MutableLiveData<ResponseGetMyDrawDetailDto>()
    val errorMessage = MutableLiveData<String>()

    fun getMyDrawDetail(courseId: Int) {
        viewModelScope.launch {
            runCatching {
                courseRepository.getMyDrawDetail(courseId)
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
                courseRepository.deleteMyDrawCourse(RequestPutMyDrawDto(deleteList))
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