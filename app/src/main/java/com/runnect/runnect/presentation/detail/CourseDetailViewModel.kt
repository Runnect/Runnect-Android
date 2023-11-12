package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourseDto
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourseDto
import com.runnect.runnect.data.dto.response.PublicCourse
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourseDto
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.domain.entity.CourseDetail
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

    private var _coursePatchState = MutableLiveData<UiStateV2<PublicCourse?>>()
    val coursePatchState: LiveData<UiStateV2<PublicCourse?>>
        get() = _coursePatchState

    private var _courseDeleteState = MutableLiveData<UiStateV2<ResponseDeleteUploadCourseDto?>>()
    val courseDeleteState: LiveData<UiStateV2<ResponseDeleteUploadCourseDto?>>
        get() = _courseDeleteState

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

    private var savedContents = CourseDetailContents("", "")

    fun updateCourseDetailContents(contents: CourseDetailContents?) {
        contents?.let {
            _title.value = contents.title
            _description.value = contents.description
        }
    }

    fun saveCurrentContents() {
        savedContents = CourseDetailContents(title, description)
    }

    fun restoreOriginalContents() {
        updateCourseDetailContents(savedContents)
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

            val requestDto = RequestPatchPublicCourseDto(
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
                RequestDeleteUploadCourseDto(publicCourseIdList = listOf(id))
            ).onSuccess { response ->
                _courseDeleteState.value = UiStateV2.Success(response)
            }.onFailure { exception ->
                _courseDeleteState.value = UiStateV2.Failure(exception.message.toString())
            }
        }
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            courseRepository.postCourseScrap(
                RequestCourseScrap(
                    publicCourseId = id, scrapTF = scrapTF.toString()
                )
            )
        }
    }

    companion object {
        private const val CODE_AUTHORIZATION_ERROR = 401
    }
}