package com.runnect.runnect.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.domain.entity.UserProfile
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository
) :
    ViewModel() {

    private val _courseScrapState = MutableLiveData<UiStateV2<Unit?>>()
    val courseScrapState: LiveData<UiStateV2<Unit?>>
        get() = _courseScrapState

    private val _userProfileState = MutableLiveData<UiStateV2<UserProfile>>()
    val userProfileState: LiveData<UiStateV2<UserProfile>>
        get() = _userProfileState

    private val _scrapCourseData = MutableLiveData<Pair<Int, Boolean>>()

    val scrapCourseData: LiveData<Pair<Int, Boolean>>
        get() = _scrapCourseData

    fun saveScrapCourseData(courseId: Int, scrapTF: Boolean) {
        _scrapCourseData.value = Pair(courseId, scrapTF)
    }

    fun getUserProfile(userId: Int) {
        viewModelScope.launch {
            _userProfileState.value = UiStateV2.Loading

            userRepository.getUserProfile(userId = userId)
                .onSuccess { profileData ->
                    if (profileData == null) {
                        _userProfileState.value = UiStateV2.Failure("PROFILE DATA IS NULL")
                        Timber.d("PROFILE DATA IS NULL")
                        return@launch
                    }
                    _userProfileState.value = UiStateV2.Success(profileData)
                    Timber.d("GET PROFILE DATA SUCCESS")
                }
                .onFailure { error ->
                    _userProfileState.value = UiStateV2.Failure(error.message.toString())
                    Timber.e("GET PROFILE DATA FAILURE")
                }
        }
    }

    fun postCourseScrap(courseId: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            _courseScrapState.value = UiStateV2.Loading

            courseRepository.postCourseScrap(
                RequestPostCourseScrap(
                    publicCourseId = courseId, scrapTF = scrapTF.toString()
                )
            ).onSuccess { response ->
                Timber.d("POST COURSE SCRAP SUCCESS")
                _courseScrapState.value = UiStateV2.Success(response)
            }.onFailure { exception ->
                Timber.e("POST COURSE SCRAP FAILURE")
                _courseScrapState.value = UiStateV2.Failure(exception.message.toString())
            }
        }
    }
}

