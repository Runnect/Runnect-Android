package com.example.runnect.presentation.storage.api.dto.response.upload


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("course")
    val course: Course
)