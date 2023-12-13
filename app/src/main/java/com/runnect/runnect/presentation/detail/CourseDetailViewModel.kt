package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourse
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.domain.entity.EditableCourseDetail
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.mode.ScreenMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository, private val userRepository: UserRepository
) : ViewModel() {
    // 서버통신 코드
    private var _courseGetState = MutableLiveData<UiStateV2<CourseDetail?>>()
    val courseGetState: LiveData<UiStateV2<CourseDetail?>>
        get() = _courseGetState

    private var _coursePatchState = MutableLiveData<UiStateV2<EditableCourseDetail?>>()
    val coursePatchState: LiveData<UiStateV2<EditableCourseDetail?>>
        get() = _coursePatchState

    private var _courseDeleteState = MutableLiveData<UiStateV2<ResponseDeleteUploadCourse?>>()
    val courseDeleteState: LiveData<UiStateV2<ResponseDeleteUploadCourse?>>
        get() = _courseDeleteState

    private var _courseScrapState = MutableLiveData<UiStateV2<Unit?>>()
    val courseScrapState: LiveData<UiStateV2<Unit?>>
        get() = _courseScrapState

    // 플래그 변수
    var isDeepLinkLogin = MutableLiveData(true)

    // 사용자가 수정할 수 있는 부분 (제목, 내용)
    val _title = MutableLiveData<String>()
    val title: String get() = _title.value ?: ""

    val _description = MutableLiveData<String>()
    val description: String get() = _description.value ?: ""

    val isValidTitle: LiveData<Boolean> = _title.map { it.isNotBlank() }
    val isValidDescription: LiveData<Boolean> = _description.map { it.isNotBlank() }

    // 액티비티에서 참조하는 변수
    private var _currentScreenMode: ScreenMode = ScreenMode.ReadOnlyMode
    val currentScreenMode get() = _currentScreenMode

    private var savedCourseDetailContents = EditableCourseDetail("", "")

    fun updateCourseDetailContents(courseDetail: EditableCourseDetail?) {
        courseDetail?.let {
            _title.value = courseDetail.title
            _description.value = courseDetail.description
        }
    }

    fun saveCurrentContents() {
        savedCourseDetailContents = EditableCourseDetail(title, description)
    }

    fun restoreOriginalContents() {
        updateCourseDetailContents(savedCourseDetailContents)
    }

    fun updateCurrentScreenMode(mode: ScreenMode) {
        _currentScreenMode = mode
    }

    fun getCourseDetail(courseId: Int) {
        viewModelScope.launch {
            _courseGetState.value = UiStateV2.Loading

            courseRepository.getCourseDetail(
                publicCourseId = courseId
            ).onSuccess { response ->
                _courseGetState.value = UiStateV2.Success(response)
            }.onFailure { exception ->
                _courseGetState.value = UiStateV2.Failure(exception.message.toString())

                if (exception is HttpException) {
                    // 딥링크로 접속했는데 로그인 되어 있지 않은 경우
                    if (exception.code() == CODE_AUTHORIZATION_ERROR) {
                        isDeepLinkLogin.value = false
                    }
                    return@launch
                }
            }
        }
    }

    fun patchPublicCourse(id: Int) {
        viewModelScope.launch {
            _coursePatchState.value = UiStateV2.Loading

            val requestDto = RequestPatchPublicCourse(
                title = title,
                description = description
            )

            courseRepository.patchPublicCourse(id, requestDto)
                .onSuccess { response ->
                    _coursePatchState.value = UiStateV2.Success(response)
                }.onFailure { exception ->
                    _coursePatchState.value = UiStateV2.Failure(exception.message.toString())
                }
        }
    }

    fun deleteUploadCourse(id: Int) {
        viewModelScope.launch {
            _courseDeleteState.value = UiStateV2.Loading

            userRepository.putDeleteUploadCourse(
                RequestDeleteUploadCourse(publicCourseIdList = listOf(id))
            ).onSuccess { response ->
                _courseDeleteState.value = UiStateV2.Success(response)
            }.onFailure { exception ->
                _courseDeleteState.value = UiStateV2.Failure(exception.message.toString())
            }
        }
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            _courseScrapState.value = UiStateV2.Loading

            courseRepository.postCourseScrap(
                RequestPostCourseScrap(
                    publicCourseId = id, scrapTF = scrapTF.toString()
                )
            ).onSuccess { response ->
                _courseScrapState.value = UiStateV2.Success(response)
            }.onFailure { exception ->
                _courseScrapState.value = UiStateV2.Failure(exception.message.toString())
            }
        }
    }

    companion object {
        private const val CODE_AUTHORIZATION_ERROR = 401
    }
}