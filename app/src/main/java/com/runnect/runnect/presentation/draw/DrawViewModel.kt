package com.runnect.runnect.presentation.draw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.model.ResponsePostCourseDto
import com.runnect.runnect.data.model.UploadLatLng
import com.runnect.runnect.util.ContentUriRequestBody
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class DrawViewModel : ViewModel() {

    val service = KApiCourse.ServicePool.courseService //객체 생성


    val path = MutableLiveData<List<UploadLatLng>>()
    var distanceSum = MutableLiveData<Float>()
    val departureAddress = MutableLiveData<String>()
    val departureName = MutableLiveData<String>()


    private val _image = MutableLiveData<ContentUriRequestBody>()
    val image: LiveData<ContentUriRequestBody>
        get() = _image

    fun setRequestBody(requestBody: ContentUriRequestBody) {
        _image.value = requestBody
    }

    val uploadResult = MutableLiveData<ResponsePostCourseDto>()
    val errorMessage = MutableLiveData<String>()


    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {

        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta))

        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515

        if (unit === "kilometer") {
            dist = dist * 1.609344
        } else if (unit === "meter") {
            dist = dist * 1609.344
        }
        return dist
    }

    // This function converts decimal degrees to radians
    fun deg2rad(deg: Double): Double {
        return (deg * Math.PI / 180.0)
    }

    // This function converts radians to decimal degrees
    fun rad2deg(rad: Double): Double {
        return (rad * 180 / Math.PI)
    }

    fun uploadCourse() {
        viewModelScope.launch {
            kotlin.runCatching {
                service.uploadCourse(_image.value!!.toFormData(), RequestBody(
                    path.value!!,
                    distanceSum.value!!,
                    departureAddress.value!!,
                    departureName.value!!
                ))
            }.onSuccess {
                uploadResult.value = it.body()
            }.onFailure {
                errorMessage.value = it.message
            }
        }
    }

    private fun RequestBody(
        path: List<UploadLatLng>,
        distance: Float,
        departureAddress: String,
        departureName: String,
    ) =
        buildJsonObject {
            val jsonElement = Json.encodeToJsonElement(path)
            put("path", jsonElement)
            put("distance", distance)
            put("departureAddress", departureAddress)
            put("departureName", departureName)
        }.toString().toRequestBody("application/json".toMediaType())

}





