package com.example.runnect.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetRewardDto(
    @SerialName("items")
    val items: List<Item>
) {
    @Serializable
    data class Item(
        @SerialName("id")
        val id: String,
        @SerialName("imgUrl")
        val imgUrl: String,
        @SerialName("name")
        val name: String
    )
}