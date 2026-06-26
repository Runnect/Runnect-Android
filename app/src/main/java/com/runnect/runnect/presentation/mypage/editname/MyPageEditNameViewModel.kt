package com.runnect.runnect.presentation.mypage.editname

import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageEditNameViewModel @Inject constructor(
    private val userRepository: UserRepository
) : MviViewModel<EditNameUiState, EditNameIntent, EditNameEffect>(EditNameUiState()) {

    override suspend fun handleIntent(intent: EditNameIntent) {
        when (intent) {
            is EditNameIntent.Init -> reduce {
                copy(nickname = intent.nickname, profileImgResId = intent.profileImgResId)
            }
            is EditNameIntent.UpdateNickname -> reduce { copy(nickname = intent.name) }
            is EditNameIntent.Submit -> submitNickname()
        }
    }

    private fun submitNickname() {
        collectFlow(
            flow = {
                userRepository.updateNickName(
                    RequestPatchNickName(nickname = currentState.nickname)
                )
            },
            onLoading = { reduce { copy(isLoading = true) } },
            onSuccess = {
                reduce { copy(isLoading = false) }
                postEffect(EditNameEffect.NavigateSuccess(currentState.nickname))
            },
            onFailure = {
                reduce { copy(isLoading = false) }
                postEffect(EditNameEffect.ShowDuplicateError)
            }
        )
    }
}
