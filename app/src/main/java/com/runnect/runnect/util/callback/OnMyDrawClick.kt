package com.runnect.runnect.util.callback

import com.runnect.runnect.data.model.ResponseGetCourseDto

interface OnMyDrawClick {
    fun selectItem(item: ResponseGetCourseDto.Data.Course)
}