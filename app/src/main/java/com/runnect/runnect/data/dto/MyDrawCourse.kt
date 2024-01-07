package com.runnect.runnect.data.dto


import android.os.Parcelable
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawCourse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyDrawCourse(
    val courseId: Int?,
    val image: String?,
    val city: String,
    val region: String,
    val title: String
) : Parcelable

fun List<ResponseGetMyDrawCourse.Data.Course>.changeMyDrawData(): List<MyDrawCourse> {
    return this.map {
        MyDrawCourse(
            courseId = it.id,
            image = it.image,
            city = it.departure.city,
            region = it.departure.region,
            title = it.title
        )
    }
}