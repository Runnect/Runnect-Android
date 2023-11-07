package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
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

    // 플래그 변수
    var isDeepLinkLogin = MutableLiveData(true)

    // todo: 현재 아이템 삭제에 필요한 변수
    private lateinit var courseToDelete: List<Int>

    // todo: 사용자로부터 입력 받는 부분 (제목, 내용)
    val _title = MutableLiveData<String>()
    val title: String get() = _title.value ?: ""

    val _contents = MutableLiveData<String>()
    val contents: String get() = _contents.value ?: ""

    // todo: 기존 방식의 코드들
//    var editTitle: MutableLiveData<String> = MutableLiveData("")
//    var editContent: MutableLiveData<String> = MutableLiveData("")
//    var isEditFinishEnable = MutableLiveData(true)

//    var titleForInterruption = MutableLiveData("")
//    var contentForInterruption = MutableLiveData("")
//    private val editMediator = MediatorLiveData<Unit>()
//
//    init {
//        editMediator.addSourceList(titleForInterruption, contentForInterruption) {
//            isEditFinishEnable.value =
//                !(titleForInterruption.value.isNullOrEmpty() or contentForInterruption.value.isNullOrEmpty())
//        }
//    }

    fun updateCourseTitle(title: String) {
        _title.value = title
    }

    fun updateCourseDescription(desc: String) {
        _contents.value = desc
    }

    fun getCourseDetail(courseId: Int) {
        viewModelScope.launch {
            _courseDetailState.value = UiStateV2.Loading

            courseRepository.getCourseDetail(
                publicCourseId = courseId
            ).onSuccess { response ->
                _courseDetailState.value = UiStateV2.Success(response)
                Timber.d("SUCCESS GET COURSE DETAIL")
            }.onFailure { t ->
                _courseDetailState.value = UiStateV2.Failure(t.message.toString())

                if (t is HttpException) {
                    // 딥링크로 접속했는데 로그인 되어 있지 않은 경우
                    if (t.code() == CODE_AUTHORIZATION_ERROR) {
                        isDeepLinkLogin.value = false
                        Timber.e("HTTP FAIL GET COURSE DETAIL: ${t.code()} ${t.message()}")
                    }
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
                        title = title,
                        description = contents
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
        private const val CODE_AUTHORIZATION_ERROR = 401
    }
}