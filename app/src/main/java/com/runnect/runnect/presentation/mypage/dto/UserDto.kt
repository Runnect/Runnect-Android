package com.runnect.runnect.presentation.mypage.dto

import com.runnect.runnect.R
import com.runnect.runnect.domain.entity.User

data class UserDto(
    val nickName: String = "",
    val email: String = "",
    val level: String = "",
    val levelPercent: Int = 0,
    val stampId: String = STAMP_LOCK,
    val profileImgResId: Int = R.drawable.user_profile_basic,
) {
    companion object {
        const val STAMP_LOCK = "lock"

        fun toDto(user: User) = UserDto(
            nickName = user.nickname,
            email = user.email,
            stampId = user.latestStamp,
            level = user.level.toString(),
            levelPercent = user.levelPercent,
        )
    }
}