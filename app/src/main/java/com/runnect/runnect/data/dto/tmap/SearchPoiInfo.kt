package com.runnect.runnect.data.dto.tmap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchPoiInfo(
    @SerialName("totalCount")
    val totalCount: String,
    @SerialName("count")
    val count: String,
    @SerialName("page")
    val page: String,
    @SerialName("pois")
    val pois: Pois
)

