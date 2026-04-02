package com.runnect.runnect.presentation.mypage

import com.runnect.runnect.R

data class MyPageUiState(
    val isLoading: Boolean = true,
    val nickname: String = "",
    val stampId: String = STAMP_LOCK,
    val profileImgResId: Int = R.drawable.user_profile_basic,
    val level: String = "",
    val levelPercent: Int = 0,
    val email: String = "",
    val error: String? = null
) {
    companion object {
        const val STAMP_LOCK = "lock"
    }
}

sealed interface MyPageIntent {
    data object LoadUserInfo : MyPageIntent
    data class UpdateNickname(val nickname: String) : MyPageIntent
    data class UpdateProfileImg(val resId: Int) : MyPageIntent
}

sealed interface MyPageEffect {
    data class ShowError(val message: String) : MyPageEffect
}
