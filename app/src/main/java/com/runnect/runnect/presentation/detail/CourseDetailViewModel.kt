package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourse
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.response.ResponseDeleteUploadCourse
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.domain.entity.EditableCourseDetail
import com.runnect.runnect.domain.entity.PostScrap
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.mode.ScreenMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository, private val userRepository: UserRepository
) : BaseViewModel() {
    // 서버통신 코드
    private val _courseGetState = MutableLiveData<UiStateV2<CourseDetail?>>()
    val courseGetState: LiveData<UiStateV2<CourseDetail?>>
        get() = _courseGetState

    private val _coursePatchState = MutableLiveData<UiStateV2<EditableCourseDetail?>>()
    val coursePatchState: LiveData<UiStateV2<EditableCourseDetail?>>
        get() = _coursePatchState

    private val _courseDeleteState = MutableLiveData<UiStateV2<ResponseDeleteUploadCourse?>>()
    val courseDeleteState: LiveData<UiStateV2<ResponseDeleteUploadCourse?>>
        get() = _courseDeleteState

    private val _courseScrapState = MutableLiveData<UiStateV2<PostScrap>>()
    val courseScrapState: LiveData<UiStateV2<PostScrap>>
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

    private var savedCourseDetail = EditableCourseDetail("", "")

    fun updateCourseDetailEditText(course: EditableCourseDetail) {
        _title.value = course.title
        _description.value = course.description
    }

    fun saveCurrentCourseDetail() {
        savedCourseDetail = EditableCourseDetail(title, description)
    }

    fun restoreOriginalCourseDetail() {
        updateCourseDetailEditText(savedCourseDetail)
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
        launchWithHandler {
            userRepository.putDeleteUploadCourse(
                RequestDeleteUploadCourse(publicCourseIdList = listOf(id))
            ).onStart {
                _courseDeleteState.value = UiStateV2.Loading
            }.collect { result ->
                result.onSuccess {
                    _courseDeleteState.value = UiStateV2.Success(it)
                }.onFailure {
                    _courseDeleteState.value = UiStateV2.Failure(it.toLog())
                }
            }
        }
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        launchWithHandler {
            val requestPostCourseScrap = RequestPostCourseScrap(
                publicCourseId = id, scrapTF = scrapTF.toString()
            )

            courseRepository.postCourseScrap(requestPostCourseScrap)
                .onStart {
                    _courseScrapState.value = UiStateV2.Loading
                }.collect { result ->
                    result.onSuccess {
                        _courseScrapState.value = UiStateV2.Success(it)
                    }.onFailure {
                        _courseScrapState.value = UiStateV2.Failure(it.toLog())
                    }
                }
        }
    }

    companion object {
        private const val CODE_AUTHORIZATION_ERROR = 401
    }
}