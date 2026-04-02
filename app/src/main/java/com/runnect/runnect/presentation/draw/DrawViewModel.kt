package com.runnect.runnect.presentation.draw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.data.dto.UploadLatLng
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.LocationData
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.ReverseGeocodingRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import com.runnect.runnect.util.multipart.ContentUriRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.acos

@HiltViewModel
class DrawViewModel @Inject constructor(
    val courseRepository: CourseRepository,
    val reverseGeocodingRepository: ReverseGeocodingRepository
) : BaseViewModel() {

    private var _drawState = MutableLiveData<UiState>(UiState.Empty)
    val drawState: LiveData<UiState>
        get() = _drawState

    val searchResult = MutableLiveData<SearchResultEntity>()

    var path: List<UploadLatLng> = listOf(
        UploadLatLng(
            37.52901832956373,
            126.9136196847032
        )
    )
    var distanceSum = MutableLiveData(0.0f)
    val isBtnAvailable = MutableLiveData(false)
    val reverseGeocodingResult = MutableLiveData<LocationData>()

    var departureAddress: String = ""
    var departureName: String = "내가 설정한 출발지"
    var courseTitle: String = ""
    var uploadCourseId: Int? = null

    private val _image = MutableLiveData<ContentUriRequestBody>()
    val image: LiveData<ContentUriRequestBody>
        get() = _image

    fun setRequestBody(requestBody: ContentUriRequestBody) {
        _image.value = requestBody
    }


    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {

        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta))

        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515

        if (unit === "kilometer") {
            dist *= 1.609344
        } else if (unit === "meter") {
            dist *= 1609.344
        }
        return dist
    }

    // This function converts decimal degrees to radians
    private fun deg2rad(deg: Double): Double {
        return (deg * Math.PI / 180.0)
    }

    // This function converts radians to decimal degrees
    private fun rad2deg(rad: Double): Double {
        return (rad * 180 / Math.PI)
    }

    fun uploadCourse() {
        launchWithHandler {
            courseRepository.uploadCourse(
                image = _image.value!!.toFormData(),
                data = CourseCreateRequestDto(
                    path = path,
                    title = courseTitle,
                    distance = distanceSum.value!!,
                    departureAddress = departureAddress,
                    departureName = departureName
                ).toRequestBody()
            ).onStart {
                _drawState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    uploadCourseId = it
                    _drawState.value = UiState.Success
                },
                onFailure = {
                    _drawState.value = UiState.Failure
                    Timber.e(it.toLog())
                }
            )
        }

    }

    fun getLocationInfoUsingLatLng(lat: Double, lon: Double) = launchWithHandler {
        reverseGeocodingRepository.getLocationInfoUsingLatLng(
            lat = lat, lon = lon
        ).collectResult(
            onSuccess = {
                reverseGeocodingResult.value = it
            },
            onFailure = {
                Timber.e(it.toLog())
            }
        )
    }
}