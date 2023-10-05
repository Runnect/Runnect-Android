package com.runnect.runnect.data.dto.tmap.geocoding


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseReverseGeocodingDto(
    @SerialName("addressInfo")
    val addressInfo: AddressInfo
) {
    @Serializable
    data class AddressInfo(
        @SerialName("addressKey")
        val addressKey: String? = null,
        @SerialName("addressType")
        val addressType: String,
        @SerialName("adminDong")
        val adminDong: String,
        @SerialName("adminDongCode")
        val adminDongCode: String,
        @SerialName("adminDongCoord")
        val adminDongCoord: AdminDongCoord? = null,
        @SerialName("buildingIndex")
        val buildingIndex: String,
        @SerialName("buildingName")
        val buildingName: String,
        @SerialName("bunji")
        val bunji: String,
        @SerialName("city_do")
        val cityDo: String,
        @SerialName("eup_myun")
        val eupMyun: String,
        @SerialName("fullAddress")
        val fullAddress: String,
        @SerialName("gu_gun")
        val guGun: String,
        @SerialName("legalDong")
        val legalDong: String,
        @SerialName("legalDongCode")
        val legalDongCode: String,
        @SerialName("legalDongCoord")
        val legalDongCoord: LegalDongCoord? = null,
        @SerialName("mappingDistance")
        val mappingDistance: String,
        @SerialName("ri")
        val ri: String,
        @SerialName("roadAddressKey")
        val roadAddressKey: String? = null,
        @SerialName("roadCode")
        val roadCode: String,
        @SerialName("roadCoord")
        val roadCoord: RoadCoord? = null,
        @SerialName("roadName")
        val roadName: String
    ) {

        @Serializable
        data class AdminDongCoord(
            @SerialName("lat")
            val lat: String,
            @SerialName("latEntr")
            val latEntr: String,
            @SerialName("lon")
            val lon: String,
            @SerialName("lonEntr")
            val lonEntr: String
        )

        @Serializable
        data class RoadCoord(
            @SerialName("lat")
            val lat: String,
            @SerialName("latEntr")
            val latEntr: String,
            @SerialName("lon")
            val lon: String,
            @SerialName("lonEntr")
            val lonEntr: String
        )

        @Serializable
        data class LegalDongCoord(
            @SerialName("lat")
            val lat: String,
            @SerialName("latEntr")
            val latEntr: String,
            @SerialName("lon")
            val lon: String,
            @SerialName("lonEntr")
            val lonEntr: String
        )
    }
}