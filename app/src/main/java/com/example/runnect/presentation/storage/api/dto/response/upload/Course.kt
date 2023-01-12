package com.example.runnect.presentation.storage.api.dto.response.upload


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Course(
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("id")
    val id: Int
)