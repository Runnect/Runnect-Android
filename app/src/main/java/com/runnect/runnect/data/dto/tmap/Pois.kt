package com.runnect.runnect.data.dto.tmap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Pois(
    @SerialName("poi")
    val poi: List<Poi>
)