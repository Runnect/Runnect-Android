package com.runnect.runnect.data.dto

import java.io.Serializable

data class HistoryInfoDTO(
    val id: Int,
    val title: String,
    val img: String,
    val location: String,
    val date: String,
    val distance: String,
    val time: String,
    val pace: String
): Serializable
