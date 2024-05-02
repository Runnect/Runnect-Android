package com.runnect.runnect.data.dto.response

import com.runnect.runnect.domain.entity.User
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetUser(
    val user: UserResponse,
) {

    @Serializable
    data class UserResponse(
        val email: String,
        val latestStamp: String,
        val level: Int,
        val levelPercent: Int,
        val nickname: String
    )

    fun toUser(): User {
        return User(
            email = user.email,
            latestStamp = user.latestStamp,
            level = user.level,
            levelPercent = user.levelPercent,
            nickname = user.nickname
        )
    }
}