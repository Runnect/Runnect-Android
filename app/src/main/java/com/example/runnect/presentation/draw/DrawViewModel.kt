package com.example.runnect.presentation.draw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runnect.presentation.storage.api.ApiCourse
import com.example.runnect.presentation.storage.api.ContentUriRequestBody
import com.example.runnect.presentation.storage.api.dto.response.upload.ResponseCourse
import kotlinx.coroutines.launch
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class DrawViewModel : ViewModel() {

    val service = ApiCourse.ServicePool.courseService //객체 생성


    val path = MutableLiveData<List<UploadLatLng>>()
    var distanceSum = MutableLiveData<Double>(0.0)
    val departureAddress = MutableLiveData<String>()
    val departureName = MutableLiveData<String>()


    private val _image = MutableLiveData<ContentUriRequestBody>()
    val image: LiveData<ContentUriRequestBody>
        get() = _image

    fun setRequestBody(requestBody: ContentUriRequestBody) {
        _image.value = requestBody
    }

    val uploadResult = MutableLiveData<ResponseCourse>()
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
                service.uploadCourse(_image.value!!.toFormData(),RequestBody(
                    path.value!!, distanceSum.value!!, departureAddress.value!!, departureName.value!!
                ))
            }.onSuccess {
                uploadResult.value = it.body()
            }.onFailure {
                errorMessage.value = it.message
            }
        }
    }
    private fun RequestBody(path : List<UploadLatLng>, distance : Double, departureAddress :String, departureName :String) = buildJsonObject {
        putJsonArray("path") {
            for (i in 1..path.size) add(i) //이게 뭔지 잘 모르겠네. 인덱스 1부터 하나씩 넣는다는 건가
        }
        put("distance", distance)
        put("departureAddress", departureAddress)
        put("departureName", departureName)
    }.toString().toRequestBody("application/json".toMediaType())




 }
