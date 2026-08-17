package com.runnect.runnect.presentation.scheme

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.storage.mydrawdetail.MyDrawDetailActivity
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.Param
import com.runnect.runnect.util.link.RunnectShareLink.KEY_PRIVATE_COURSE_ID
import com.runnect.runnect.util.link.RunnectShareLink.KEY_PUBLIC_COURSE_ID
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SchemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (isUserLoggedIn()) {
            handleAppLink()
        } else {
            navigateToLoginScreen()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val accessToken = PreferenceManager.getString(this, TOKEN_KEY_ACCESS)
        return accessToken != "none"
    }

    private fun handleAppLink() {
        val link = intent.data
        if (link == null) {
            Timber.e("FAIL: no data in intent")
            finish()
            return
        }

        val publicCourseId = getCourseId(link, KEY_PUBLIC_COURSE_ID)
        val privateCourseId = getCourseId(link, KEY_PRIVATE_COURSE_ID)

        val targetScreen = when {
            publicCourseId != null -> "CourseDetail"
            privateCourseId != null -> "MyDrawDetail"
            else -> "unknown"
        }
        Analytics.logEvent(
            EventName.SYS_DEEPLINK_OPEN,
            Param.DEEPLINK_URL to link.toString(),
            Param.TARGET_SCREEN to targetScreen
        )

        when {
            publicCourseId != null -> navigateToCourseDetail<CourseDetailActivity>(publicCourseId)
            privateCourseId != null -> navigateToCourseDetail<MyDrawDetailActivity>(privateCourseId)
            else -> {
                Timber.e("FAIL: could not find course id")
                finish()
            }
        }
    }

    private fun getCourseId(link: Uri, key: String): Int? {
        return link.getQueryParameter(key)?.toIntOrNull()
    }

    private inline fun <reified T : Activity> navigateToCourseDetail(courseId: Int) {
        Intent(this, T::class.java).apply {
            putExtra(EXTRA_FROM_APP_LINK, courseId)
            startActivity(this)
        }
    }

    private fun navigateToLoginScreen() {
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    companion object {
        const val EXTRA_FROM_APP_LINK = "fromAppLink"
        private const val TOKEN_KEY_ACCESS = "access"
    }
}
