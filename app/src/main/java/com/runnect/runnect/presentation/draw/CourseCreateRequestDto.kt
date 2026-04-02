package com.runnect.runnect.presentation.draw

import com.runnect.runnect.data.dto.UploadLatLng
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

data class CourseCreateRequestDto(
    val path: List<UploadLatLng>,
    val title: String,
    val distance: Float,
    val departureAddress: String,
    val departureName: String
)

fun CourseCreateRequestDto.toRequestBody(): okhttp3.RequestBody {
    val json = buildJsonObject {
        put("path", Json.encodeToJsonElement(path))
        put("title", title)
        put("distance", distance)
        put("departureAddress", departureAddress)
        put("departureName", departureName)
    }.toString()
    return json.toRequestBody("application/json".toMediaType())
}