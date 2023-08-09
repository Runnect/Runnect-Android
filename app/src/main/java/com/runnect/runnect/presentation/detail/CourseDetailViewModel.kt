package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.CourseDetailDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestUpdatePublicCourse
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.addSourceList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository, private val userRepository: UserRepository
) : ViewModel() {
    private var _courseDetailState = MutableLiveData<UiState>(UiState.Loading)
    val courseDetailState: LiveData<UiState>
        get() = _courseDetailState

    private var _myUploadDeleteState = MutableLiveData<UiState>()
    val myUploadDeleteState: LiveData<UiState>
        get() = _myUploadDeleteState

    private var _courseUpdateState = MutableLiveData<UiState>()
    val courseUpdateState: LiveData<UiState>
        get() = _courseUpdateState

    private lateinit var courseToDelete: List<Int>

    var imageUrl: MutableLiveData<String> = MutableLiveData("")
    var title: MutableLiveData<String> = MutableLiveData("")
    var description: MutableLiveData<String> = MutableLiveData("")

    var stampId: MutableLiveData<String> = MutableLiveData("CSPR0")
    var mapImage: MutableLiveData<String> = MutableLiveData(DEFAULT_MAP_IMAGE)
    var editTitle: MutableLiveData<String> = MutableLiveData("")
    var editContent: MutableLiveData<String> = MutableLiveData("")
    var distance: MutableLiveData<String> = MutableLiveData("")
    var departure: MutableLiveData<String> = MutableLiveData("")
    var isEditFinishEnable = MutableLiveData(true)
    var titleForInterruption = MutableLiveData("")
    var contentForInterruption = MutableLiveData("")

    var isFromDeepLink: MutableLiveData<Boolean> = MutableLiveData(false)
    val editMediator = MediatorLiveData<Unit>()
    var isEdited = false

    init {
        editMediator.addSourceList(titleForInterruption, contentForInterruption) {
            isEditFinishEnable.value =
                !(titleForInterruption.value.isNullOrEmpty() or contentForInterruption.value.isNullOrEmpty())
        }
    }

    val editState: LiveData<UiState>
        get() = _editState
    private val _editState = MutableLiveData<UiState>()

    private var _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean>
        get() = _editMode

    private var _courseDetail = CourseDetailDTO(
        "CSPR0", "1", "1", 1, "1", "1", "1", 1, "1", false, "1", listOf(listOf(1.1))
    )

    val courseDetail: CourseDetailDTO
        get() = _courseDetail

    val errorMessage = MutableLiveData<String>()

    fun convertMode() {
        _editMode.value = !_editMode.value!!
    }

    fun getCourseDetail(courseId: Int) {
        viewModelScope.launch {
            runCatching {
                _courseDetailState.value = UiState.Loading
                courseRepository.getCourseDetail(
                    publicCourseId = courseId
                )
            }.onSuccess {
                imageUrl.value = it.image
                title.value = it.title
                description.value = it.description

                _courseDetail = it
                stampId.value = it.stampId
                mapImage.value = it.image
                editTitle.value = it.title
                editContent.value = it.description
                distance.value = it.distance
                departure.value = it.departure
                _courseDetailState.value = UiState.Success
            }.onFailure {
                _courseDetailState.value = UiState.Failure
                errorMessage.value = it.message

                if(errorMessage.value == "HTTP 401 Unauthorized"){
                    isFromDeepLink.value = true
                }
            }
        }
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            runCatching {
                courseRepository.postCourseScrap(
                    RequestCourseScrap(
                        publicCourseId = id, scrapTF = scrapTF.toString()
                    )
                )
            }.onSuccess {}.onFailure {
                errorMessage.value = it.message
            }
        }
    }

    fun deleteUploadCourse(id: Int) {
        courseToDelete = listOf(id)
        viewModelScope.launch {
            runCatching {
                _myUploadDeleteState.value = UiState.Loading
                userRepository.putDeleteUploadCourse(
                    RequestDeleteUploadCourse(
                        publicCourseIdList = courseToDelete
                    )
                )
            }.onSuccess {
                _myUploadDeleteState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _myUploadDeleteState.value = UiState.Failure
            }
        }
    }

    fun patchUpdatePublicCourse(id: Int) {
        viewModelScope.launch {
            runCatching {
                _courseUpdateState.value = UiState.Loading
                courseRepository.patchUpdatePublicCourse(
                    id, RequestUpdatePublicCourse(
                        description = contentForInterruption.value!!,
                        title = titleForInterruption.value!!
                    )
                )
            }.onSuccess {
                _courseUpdateState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _courseUpdateState.value = UiState.Failure
            }
        }
    }

    companion object {
        const val DEFAULT_MAP_IMAGE =
            "https://insopt-bucket-rin.s3.ap-northeast-2.amazonaws.com/1682263387937_temprentpk745355331421921473.png"
    }
}