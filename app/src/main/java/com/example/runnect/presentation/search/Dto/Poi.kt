package com.example.runnect.presentation.search.Dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Poi(
    //POI 의  id
    @SerialName("id")
    val id: String? = null,

    //POI 의 name
    @SerialName("name")
    val name: String? = null,

    //POI 에 대한 전화번호
    @SerialName("telNo")
    val telNo: String? = null,

    //시설물 입구 위도 좌표
    @SerialName("frontLat")
    val frontLat: Float = 0.0f,

    //시설물 입구 경도 좌표
    @SerialName("frontLon")
    val frontLon: Float = 0.0f,

    //중심점 위도 좌표
    @SerialName("noorLat")
    val noorLat: Float = 0.0f,

    //중심점 경도 좌표
    @SerialName("noorLon")
    val noorLon: Float = 0.0f,

    //표출 주소 대분류명
    @SerialName("upperAddrName")
    val upperAddrName: String? = null,

    //표출 주소 중분류명
    @SerialName("middleAddrName")
    val middleAddrName: String? = null,

    //표출 주소 소분류명
    @SerialName("lowerAddrName")
    val lowerAddrName: String? = null,

    //표출 주소 세분류명
    @SerialName("detailAddrName")
    val detailAddrName: String? = null,

    //본번
    @SerialName("firstNo")
    val firstNo: String? = null,

    //부번
    @SerialName("secondNo")
    val secondNo: String? = null,

    //도로명
    @SerialName("roadName")
    val roadName: String? = null,

    //건물번호 1
    @SerialName("firstBuildNo")
    val firstBuildNo: String? = null,

    //건물번호 2
    @SerialName("secondBuildNo")
    val secondBuildNo: String? = null,

    //업종 대분류명
    @SerialName("mlClass")
    val mlClass: String? = null,

    //거리(km)
    @SerialName("radius")
    val radius: String? = null,

    //업소명
    @SerialName("bizName")
    val bizName: String? = null,

    //시설목적
    @SerialName("upperBizName")
    val upperBizName: String? = null,

    //시설분류
    @SerialName("middleBizName")
    val middleBizName: String? = null,

    //시설이름 ex) 지하철역 병원 등
    @SerialName("lowerBizName")
    val lowerBizName: String? = null,

    //상세 이름
    @SerialName("detailBizName")
    val detailBizName: String? = null,

    //길안내 요청 유무
    @SerialName("rpFlag")
    val rpFlag: String? = null,

    //주차 가능유무
    @SerialName("parkFlag")
    val parkFlag: String? = null,

    //POI 상세정보 유무
    @SerialName("detailInfoFlag")
    val detailInfoFlag: String? = null,

    //소개 정보
    @SerialName("desc")
    val desc: String? = null
)
