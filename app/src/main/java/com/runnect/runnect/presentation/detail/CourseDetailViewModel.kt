package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestUpdatePublicCourse
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.extension.addSourceList
import com.runnect.runnect.util.mode.ScreenMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository, private val userRepository: UserRepository
) : ViewModel() {
    // TODO: 서버통신 코드 UiState 통일시키기
    private var _courseDetailState = MutableLiveData<UiStateV2<CourseDetail?>>()
    val courseDetailState: LiveData<UiStateV2<CourseDetail?>>
        get() = _courseDetailState

    private var _myUploadDeleteState = MutableLiveData<UiState>()
    val myUploadDeleteState: LiveData<UiState>
        get() = _myUploadDeleteState

    private var _courseUpdateState = MutableLiveData<UiState>()
    val courseUpdateState: LiveData<UiState>
        get() = _courseUpdateState

    // todo: 딥링크 관련 변수
    var isDeepLinkLogin = MutableLiveData(true)

    // todo: 액티비티에서 참조할 변수들


    // todo: 현재 아이템 삭제에 필요한 변수
    private lateinit var courseToDelete: List<Int>

//    var imageUrl: MutableLiveData<String> = MutableLiveData("")
//    var title: MutableLiveData<String> = MutableLiveData("")
//    var description: MutableLiveData<String> = MutableLiveData("")
//    var stampId: MutableLiveData<String> = MutableLiveData("CSPR0")
//    var mapImage: MutableLiveData<String> = MutableLiveData(DEFAULT_MAP_IMAGE)

    // todo: 사용자로부터 입력 받는 부분 (제목, 내용)
    val _title = MutableLiveData<String>()
    val title: String get() = _title.value ?: ""

    val _desc = MutableLiveData<String>()
    val desc: String get() = _desc.value ?: ""

    // todo: 기존 방식의 코드들
    var editTitle: MutableLiveData<String> = MutableLiveData("")
    var editContent: MutableLiveData<String> = MutableLiveData("")
    var isEditFinishEnable = MutableLiveData(true)
    var titleForInterruption = MutableLiveData("")
    var contentForInterruption = MutableLiveData("")
    private val editMediator = MediatorLiveData<Unit>()
    var isEdited = false

    init {
        editMediator.addSourceList(titleForInterruption, contentForInterruption) {
            isEditFinishEnable.value =
                !(titleForInterruption.value.isNullOrEmpty() or contentForInterruption.value.isNullOrEmpty())
        }
    }

    fun updateCourseTitle(title: String) {
        _title.value = title
    }

    fun updateCourseDescription(desc: String) {
        _desc.value = desc
    }

    fun getCourseDetail(courseId: Int) {
        viewModelScope.launch {
            _courseDetailState.value = UiStateV2.Loading

            courseRepository.getCourseDetail(
                publicCourseId = courseId
            ).onSuccess { response ->
                _courseDetailState.value = UiStateV2.Success(response)
                Timber.d("SUCCESS GET COURSE DETAIL")

//                imageUrl.value = it.image
//                title.value = it.title
//                description.value = it.description
//                _courseDetail = it
//                stampId.value = it.stampId
//                mapImage.value = it.image

//                editTitle.value = it.title
//                editContent.value = it.description

//                distance.value = it.distance
//                departure.value = it.departure
            }.onFailure { t ->
                _courseDetailState.value = UiStateV2.Failure(t.message.toString())

                if (t is HttpException) {
                    // 딥링크 접속 && 미로그인의 경우
                    if (t.code() == CODE_AUTHORIZATION_ERROR) {
                        isDeepLinkLogin.value = false
                    }

                    Timber.e("HTTP FAIL GET COURSE DETAIL: ${t.code()} ${t.message()}")
                    return@launch
                }

                Timber.e("FAIL GET COURSE DETAIL: ${t.message}")
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
            }.onSuccess {

            }.onFailure {

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
                _courseUpdateState.value = UiState.Failure
            }
        }
    }

    companion object {
        private const val DEFAULT_MAP_IMAGE =
            "https://insopt-bucket-rin.s3.ap-northeast-2.amazonaws.com/1682263387937_temprentpk745355331421921473.png"

        private const val CODE_AUTHORIZATION_ERROR = 401
    }
}