package com.runnect.runnect.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetBanner(
    @SerialName("banners")
    val banners: List<Banner>
) {
    @Serializable
    data class Banner(
        @SerialName("index")
        val index: Int,
        @SerialName("imageUrl")
        val imageUrl: String,
        @SerialName("linkUrl")
        val linkUrl: String
    )
}
