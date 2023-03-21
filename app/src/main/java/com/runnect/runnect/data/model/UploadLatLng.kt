package com.runnect.runnect.data.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UploadLatLng(
    @SerialName("lat")
    val lat: Double,
    @SerialName("long")
    val long: Double,
)