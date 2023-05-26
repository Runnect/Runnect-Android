package com.runnect.runnect.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPutMyDrawDto(
    @SerialName("courseIdList")
    val courseIdList: List<Int>
)