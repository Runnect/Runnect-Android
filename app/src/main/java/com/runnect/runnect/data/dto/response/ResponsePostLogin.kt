package com.runnect.runnect.data.dto.response

import com.runnect.runnect.data.dto.LoginDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostLogin(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("email")
    val email: String,
    @SerialName("nickname")
    val nickName: String = "",
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("type")
    val type: String,
) {
    fun toData(): LoginDTO {
        return LoginDTO(
            accessToken = accessToken,
            refreshToken = refreshToken,
            email = email,
            type = type
        )
    }
}