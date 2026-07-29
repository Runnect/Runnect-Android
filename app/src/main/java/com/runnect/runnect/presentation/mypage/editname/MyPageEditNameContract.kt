package com.runnect.runnect.presentation.mypage.editname

import com.runnect.runnect.R

data class EditNameUiState(
    val nickname: String = "",
    val profileImgResId: Int = R.drawable.user_profile_basic,
    val isLoading: Boolean = false,
)

sealed interface EditNameIntent {
    data class Init(val nickname: String, val profileImgResId: Int) : EditNameIntent
    data class UpdateNickname(val name: String) : EditNameIntent
    data object Submit : EditNameIntent
}

sealed interface EditNameEffect {
    data class NavigateSuccess(val newNickname: String) : EditNameEffect
    data object ShowDuplicateError : EditNameEffect
}
