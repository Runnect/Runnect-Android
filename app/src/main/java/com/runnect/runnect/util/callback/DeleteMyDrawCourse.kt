package com.runnect.runnect.util.callback

import com.runnect.runnect.data.model.RequestPutMyDrawDto

interface DeleteMyDrawCourse {
    fun deleteCourse(deleteList: MutableList<Long>)
}