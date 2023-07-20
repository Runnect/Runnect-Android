package com.runnect.runnect.data.dto.tmap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseTmapDto(
    @SerialName("searchPoiInfo")
    val searchPoiInfo: SearchPoiInfo,
)
