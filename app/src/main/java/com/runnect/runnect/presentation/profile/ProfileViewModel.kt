package com.runnect.runnect.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.PostScrap
import com.runnect.runnect.domain.entity.UserProfile
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository
) : BaseViewModel() {

    private val _courseScrapState = MutableLiveData<UiStateV2<PostScrap>>()
    val courseScrapState: LiveData<UiStateV2<PostScrap>>
        get() = _courseScrapState

    private val _userProfileState = MutableLiveData<UiStateV2<UserProfile>>()
    val userProfileState: LiveData<UiStateV2<UserProfile>>
        get() = _userProfileState


    fun getUserProfile(userId: Int) = launchWithHandler {
        userRepository.getUserProfile(userId)
            .onStart {
                _userProfileState.value = UiStateV2.Loading
            }.collectResult(
                onSuccess = {
                    _userProfileState.value = UiStateV2.Success(it)
                },
                onFailure = {
                    _userProfileState.value = UiStateV2.Failure(it.toLog())
                }
            )
    }

    fun postCourseScrap(courseId: Int, scrapTF: Boolean) = launchWithHandler {
        val requestPostCourseScrap = RequestPostCourseScrap(
            publicCourseId = courseId,
            scrapTF = scrapTF.toString()
        )

        courseRepository.postCourseScrap(requestPostCourseScrap)
            .onStart {
                _courseScrapState.value = UiStateV2.Loading
            }.collectResult(
                onSuccess = {
                    Timber.d("POST COURSE SCRAP SUCCESS")
                    _courseScrapState.value = UiStateV2.Success(it)
                },
                onFailure = {
                    Timber.e("POST COURSE SCRAP FAILURE")
                    _courseScrapState.value = UiStateV2.Failure(it.toLog())
                }
            )
    }
}

