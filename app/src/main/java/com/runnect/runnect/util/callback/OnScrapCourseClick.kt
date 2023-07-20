package com.runnect.runnect.util.callback

import com.runnect.runnect.data.dto.MyScrapCourse

interface OnScrapCourseClick {
    fun selectItem(item: MyScrapCourse)
}