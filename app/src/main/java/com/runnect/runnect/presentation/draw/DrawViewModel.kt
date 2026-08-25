package com.runnect.runnect.presentation.draw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.data.dto.UploadLatLng
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.LocationData
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.MarkerQuotaRepository
import com.runnect.runnect.domain.repository.ReverseGeocodingRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import com.runnect.runnect.util.multipart.ContentUriRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.math.acos

@HiltViewModel
class DrawViewModel @Inject constructor(
    val courseRepository: CourseRepository,
    val reverseGeocodingRepository: ReverseGeocodingRepository,
    val markerQuotaRepository: MarkerQuotaRepository
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

    private val _markerQuotaBalance = MutableLiveData<Int>()
    val markerQuotaBalance: LiveData<Int>
        get() = _markerQuotaBalance

    private val _markerRewardGrantState = MutableLiveData<UiState>(UiState.Empty)
    val markerRewardGrantState: LiveData<UiState>
        get() = _markerRewardGrantState

    /**
     * 마커 재화가 부족해 코스 저장이 막혔을 때 1회성으로 쏘는 이벤트.
     * drawState(UiState)는 ViewModel 생존 중 값이 유지되는 LiveData라, 액티비티가
     * 재생성되면 관찰을 다시 시작하는 순간 이전 값을 다시 흘려보낸다(sticky). 그래서
     * 부족 알림은 별도 LiveData로 분리하고, 처리한 즉시 false로 되돌려 재생을 막는다.
     */
    private val _markerQuotaInsufficientEvent = MutableLiveData(false)
    val markerQuotaInsufficientEvent: LiveData<Boolean>
        get() = _markerQuotaInsufficientEvent

    fun consumeMarkerQuotaInsufficientEventHandled() {
        _markerQuotaInsufficientEvent.value = false
    }

    fun fetchMarkerQuota() = launchWithHandler {
        markerQuotaRepository.getMarkerQuota().collectResult(
            onSuccess = {
                _markerQuotaBalance.value = it.balance
            },
            onFailure = {
                Timber.e(it.toLog())
            }
        )
    }

    /**
     * 코스 저장. 이번 코스에서 실제로 사용한 마커 개수만큼 서버 잔액을 먼저 차감하고,
     * 성공했을 때만 실제 업로드를 진행한다. "차감 성공"과 "업로드 시작"을 액티비티 쪽의
     * 서로 다른 LiveData 관찰로 나누면, 액티비티가 재생성될 때 살아있는 ViewModel의
     * LiveData가 이전 성공 값을 다시 흘려보내(sticky) 업로드가 중복 호출될 수 있다 —
     * 그래서 이 흐름 전체를 하나의 코루틴 안에서 순차 실행해 그럴 여지를 없앤다.
     * 업로드가 실패하면 이미 차감한 마커를 되돌려줘야, 저장되지 않은 코스 때문에
     * 사용자가 마커만 잃는 일이 없다.
     */
    fun saveCourse(markerCount: Int) {
        _drawState.value = UiState.Loading
        launchWithHandler {
            markerQuotaRepository.consumeMarkerQuota(markerCount).collectResult(
                onSuccess = {
                    _markerQuotaBalance.value = it.balance
                    uploadCourse(consumedMarkerCount = markerCount)
                },
                onFailure = {
                    _drawState.value = UiState.Failure
                    _markerQuotaInsufficientEvent.value = true
                    Timber.e(it.toLog())
                }
            )
        }
    }

    private fun uploadCourse(consumedMarkerCount: Int) {
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
            ).collectResult(
                onSuccess = {
                    uploadCourseId = it
                    _drawState.value = UiState.Success
                },
                onFailure = {
                    _drawState.value = UiState.Failure
                    Timber.e(it.toLog())
                    refundMarkerQuota(consumedMarkerCount)
                }
            )
        }
    }

    /**
     * 업로드 실패로 저장되지 않은 코스에 대해, 저장 시점에 미리 차감했던 마커를 되돌려준다.
     * 전용 환불 API가 없어 광고 리워드 지급 API를 재사용한다 — 지급 성공/실패 판단이
     * rewardTransactionId 유니크 제약 기반 멱등 처리로 이미 구현돼 있어, 이 요청이
     * 네트워크 재시도로 중복 전송되어도 마커가 두 번 환불되지 않는다.
     */
    private fun refundMarkerQuota(amount: Int) {
        launchWithHandler {
            markerQuotaRepository.grantMarkerReward(
                amount = amount,
                rewardTransactionId = "upload-fail-refund-${UUID.randomUUID()}"
            ).collectResult(
                onSuccess = { _markerQuotaBalance.value = it.balance },
                onFailure = { Timber.e(it.toLog()) }
            )
        }
    }

    fun grantMarkerRewardFromAd(rewardTransactionId: String) {
        _markerRewardGrantState.value = UiState.Loading
        launchWithHandler {
            markerQuotaRepository.grantMarkerReward(
                amount = MARKER_REWARD_AMOUNT,
                rewardTransactionId = rewardTransactionId
            ).collectResult(
                onSuccess = {
                    _markerQuotaBalance.value = it.balance
                    _markerRewardGrantState.value = UiState.Success
                },
                onFailure = {
                    _markerRewardGrantState.value = UiState.Failure
                    Timber.e(it.toLog())
                }
            )
        }
    }

    companion object {
        const val MARKER_REWARD_AMOUNT = 5
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