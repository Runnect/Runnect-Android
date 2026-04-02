package com.runnect.runnect.presentation.mypage

import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository
) : MviViewModel<MyPageUiState, MyPageIntent, MyPageEffect>(MyPageUiState()) {

    override suspend fun handleIntent(intent: MyPageIntent) {
        when (intent) {
            is MyPageIntent.LoadUserInfo -> loadUserInfo()
            is MyPageIntent.UpdateNickname -> reduce { copy(nickname = intent.nickname) }
            is MyPageIntent.UpdateProfileImg -> reduce { copy(profileImgResId = intent.resId) }
        }
    }

    private fun loadUserInfo() {
        collectFlow(
            flow = { userRepository.getUserInfo() },
            onLoading = {
                reduce { copy(isLoading = true, error = null) }
            },
            onSuccess = { user ->
                reduce {
                    copy(
                        isLoading = false,
                        nickname = user.nickname,
                        stampId = user.latestStamp,
                        level = user.level.toString(),
                        levelPercent = user.levelPercent,
                        email = user.email,
                        error = null
                    )
                }
            },
            onFailure = { throwable ->
                val message = throwable.toLog()
                reduce { copy(isLoading = false, error = message) }
                postEffect(MyPageEffect.ShowError(message))
            }
        )
    }
}
