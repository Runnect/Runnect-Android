package com.runnect.runnect.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.UserProfileData
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {


    private val _userProfileState = MutableLiveData<UiStateV2<UserProfileData>>()
    val userProfileState: LiveData<UiStateV2<UserProfileData>>
        get() = _userProfileState

    fun getUserProfile(userId: Int) {
        viewModelScope.launch {
            _userProfileState.value = UiStateV2.Loading

            userRepository.getUserProfile(userId = userId)
                .onSuccess { profileData ->
                    if (profileData == null) {
                        _userProfileState.value = UiStateV2.Failure("PROFILE DATA IS NULL")
                        Timber.e("PROFILE DATA IS NULL")
                        return@launch
                    }
                    _userProfileState.value = UiStateV2.Success(profileData)
                    Timber.e("GET PROFILE DATA SUCCESS")
                }
                .onFailure { error ->
                    _userProfileState.value = UiStateV2.Failure(error.message.toString())
                    Timber.e("GET PROFILE DATA FAILURE")
                }
        }
    }
}

