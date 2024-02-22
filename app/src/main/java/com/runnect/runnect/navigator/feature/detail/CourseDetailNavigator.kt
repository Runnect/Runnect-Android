package com.runnect.runnect.navigator.feature.detail

import com.runnect.runnect.navigator.base.Navigator

interface CourseDetailNavigator : Navigator {
    companion object {
        const val EXTRA_PUBLIC_COURSE_ID = "EXTRA_PUBLIC_COURSE_ID"
    }
}