package com.example.runnect.presentation.storage.api.dto


import com.naver.maps.geometry.LatLng


data class CourseData(

    val path: MutableList<LatLng>
)