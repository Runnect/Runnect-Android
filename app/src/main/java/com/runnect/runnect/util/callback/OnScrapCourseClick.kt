package com.runnect.runnect.util.callback

import com.runnect.runnect.data.model.MyScrapCourse

interface OnScrapCourseClick {
    fun selectItem(item: MyScrapCourse)
}