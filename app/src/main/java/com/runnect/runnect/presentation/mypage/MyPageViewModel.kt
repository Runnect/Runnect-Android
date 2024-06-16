package com.runnect.runnect.presentation.mypage

import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.mypage.dto.UserDto
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    sealed interface UserState {
        object Loading : UserState
        object Success : UserState
        data class Failure(val message: String) : UserState
    }

    private val _userState: MutableStateFlow<UserState> = MutableStateFlow(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _userData = MutableStateFlow(UserDto())
    val userData: StateFlow<UserDto> = _userData.asStateFlow()


    fun updateUser(user: UserDto) {
        _userState.value = UserState.Success.also {
            _userData.value = user
        }
    }

    fun getUserInfo() = launchWithHandler {
        userRepository.getUserInfo()
            .flowOn(Dispatchers.IO)
            .onStart {
                _userState.tryEmit(UserState.Loading)
            }.collectResult (
                onSuccess = { user ->
                    val userDto = UserDto.toDto(user)
                    updateUser(userDto)
                },
                onFailure = {
                    _userState.value = UserState.Failure(it.toLog())
                    sendEvent(EventState.ShowSnackBar(it.toLog()))
                }
            )
    }
}