package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.entity.CourseDetail
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.data.dto.request.RequestPatchPublicCourseDto
import com.runnect.runnect.data.dto.response.PublicCourse
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
    private var _courseGetState = MutableLiveData<UiStateV2<CourseDetail?>>()
    val courseGetState: LiveData<UiStateV2<CourseDetail?>>
        get() = _courseGetState

    private var _coursePatchState = MutableLiveData<UiStateV2<PublicCourse?>>()
    val coursePatchState: LiveData<UiStateV2<PublicCourse?>>
        get() = _coursePatchState

    private var _myUploadDeleteState = MutableLiveData<UiState>()
    val myUploadDeleteState: LiveData<UiState>
        get() = _myUploadDeleteState

    // 플래그 변수
    var isDeepLinkLogin = MutableLiveData(true)

    // todo: 현재 아이템 삭제에 필요한 변수
    private lateinit var courseToDelete: List<Int>

    // todo: 사용자로부터 입력 받는 부분 (제목, 내용)
    val _title = MutableLiveData<String>()
    val title: String get() = _title.value ?: ""

    val _description = MutableLiveData<String>()
    val description: String get() = _description.value ?: ""

    val isValidTitle: LiveData<Boolean> = _title.map { it.isNotBlank() }
    val isValidDescription: LiveData<Boolean> = _description.map { it.isNotBlank() }

    // todo: 액티비티에서 참조하는 변수들
    private var savedContents = CourseDetailContents("", "")

    private var _currentScreenMode: ScreenMode = ScreenMode.ReadOnlyMode
    val currentScreenMode get() = _currentScreenMode

    fun updateCourseTitle(title: String) {
        _title.value = title
    }

    fun updateCourseDescription(desc: String) {
        _description.value = desc
    }

    fun saveCurrentContents() {
        savedContents = CourseDetailContents(title, description)
    }

    fun restoreOriginalContents() {
        _title.value = savedContents.title
        _description.value = savedContents.description
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
                Timber.d("SUCCESS GET COURSE DETAIL")
            }.onFailure { t ->
                _courseGetState.value = UiStateV2.Failure(t.message.toString())

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
                    Timber.d("SUCCESS PATCH COURSE DETAIL CONTENTS")
                }.onFailure { t ->
                    _coursePatchState.value = UiStateV2.Failure(t.message.toString())

                    if (t is HttpException) {
                        Timber.e("HTTP FAIL PATCH COURSE DETAIL CONTENTS: ${t.code()} ${t.message()}")
                        return@launch
                    }

                    Timber.e("FAIL PATCH COURSE DETAIL CONTENTS: ${t.message}")
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

    companion object {
        private const val CODE_AUTHORIZATION_ERROR = 401
    }
}