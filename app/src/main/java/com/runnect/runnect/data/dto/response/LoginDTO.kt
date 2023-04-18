package com.runnect.runnect.data.dto.response

data class LoginDTO(
    val status: Int,
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val type: String
)
