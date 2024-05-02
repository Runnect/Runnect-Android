package com.runnect.runnect.data.dto

data class LoginDTO(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val type: String
)
