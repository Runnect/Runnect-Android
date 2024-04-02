package com.runnect.runnect.presentation.detail.navigator.impl

import com.runnect.runnect.navigator.feature.detail.CourseDetailNavigator
import android.app.Activity
import android.content.Intent
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.util.extension.startActivityWithAnimation
import javax.inject.Inject

internal class CourseDetailNavigatorImpl @Inject constructor() : CourseDetailNavigator {
    override fun navigateFrom(
        activity: Activity,
        intentBuilder: Intent.() -> Intent,
        withFinish: Boolean
    ) {
        activity.startActivityWithAnimation<CourseDetailActivity>(
            intentBuilder = intentBuilder,
            withFinish = withFinish,
        )
    }
}