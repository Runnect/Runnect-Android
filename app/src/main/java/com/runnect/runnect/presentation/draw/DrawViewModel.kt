package com.runnect.runnect.presentation.draw

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.LocationData
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.data.dto.UploadLatLng
import com.runnect.runnect.data.dto.response.ResponsePostCourseDto
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.domain.ReverseGeocodingRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.ContentUriRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DrawViewModel @Inject constructor(
    val courseRepository: CourseRepository,
    val reverseGeocodingRepository: ReverseGeocodingRepository
) : ViewModel() {

    private var _drawState = MutableLiveData<UiState>(UiState.Empty)
    val drawState: LiveData<UiState>
        get() = _drawState

    val searchResult = MutableLiveData<SearchResultEntity>()

    val path = MutableLiveData<List<UploadLatLng>>()
    var distanceSum = MutableLiveData(0.0f)
    val departureAddress = MutableLiveData<String>()
    val departureName = MutableLiveData<String>()
    val isBtnAvailable = MutableLiveData(false)

    val reverseGeocodingResult = MutableLiveData<LocationData>()


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
            runCatching {
                _drawState.value = UiState.Loading
                courseRepository.uploadCourse(
                    image = _image.value!!.toFormData(),
                    data = RequestBody(
                        path = path.value!!,
                        distance = distanceSum.value!!,
                        departureAddress = departureAddress.value!!,
                        departureName = departureName.value!!
                    )
                )
            }.onSuccess {
                Timber.tag(ContentValues.TAG).d("통신success")
                uploadResult.value = it.body()
                _drawState.value = UiState.Success
            }.onFailure {
                Timber.tag(ContentValues.TAG).d("통신failure : ${it}")
                errorMessage.value = it.message
                _drawState.value = UiState.Failure
            }
        }
    }

    fun getLocationInfoUsingLatLng(lat: Double, lon: Double) {
        viewModelScope.launch {
            runCatching {
                reverseGeocodingRepository.getLocationInfoUsingLatLng(
                    lat = lat, lon = lon
                )
            }.onSuccess {
                Timber.tag(ContentValues.TAG).d("통신success")
                reverseGeocodingResult.value = it
            }.onFailure {
                Timber.tag(ContentValues.TAG).d("통신failure : ${it}")
                errorMessage.value = it.message
            }
        }
    }

    private fun RequestBody(
        path: List<UploadLatLng>,
        distance: Float,
        departureAddress: String,
        departureName: String,
    ) = buildJsonObject {
        val jsonElement = Json.encodeToJsonElement(path)
        put("path", jsonElement)
        put("distance", distance)
        put("departureAddress", departureAddress)
        put("departureName", departureName)
    }.toString().toRequestBody("application/json".toMediaType())

}





