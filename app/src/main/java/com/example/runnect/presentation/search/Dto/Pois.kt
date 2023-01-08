package com.example.runnect.presentation.search.Dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Pois(
    @SerialName("poi")
    val poi: List<Poi>
)