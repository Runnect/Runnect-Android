package com.runnect.runnect.presentation.scheme

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.runnect.runnect.application.PreferenceManager
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.storage.mydrawdetail.MyDrawDetailActivity
import com.runnect.runnect.util.dynamiclink.RunnectDynamicLink.KEY_PRIVATE_COURSE_ID
import com.runnect.runnect.util.dynamiclink.RunnectDynamicLink.KEY_PUBLIC_COURSE_ID
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SchemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isUserLoggedIn()) {
            handleDynamicLinks()
        } else {
            navigateToLoginScreen()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val accessToken = PreferenceManager.getString(this, TOKEN_KEY_ACCESS)
        return accessToken != "none"
    }

    private fun handleDynamicLinks() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                val link = pendingDynamicLinkData.link

                if (link != null) {
                    val publicCourseId = getCourseId(link, KEY_PUBLIC_COURSE_ID)
                    val privateCourseId = getCourseId(link, KEY_PRIVATE_COURSE_ID)

                    when {
                        publicCourseId != null -> navigateToCourseDetail<CourseDetailActivity>(
                            publicCourseId
                        )

                        privateCourseId != null -> navigateToCourseDetail<MyDrawDetailActivity>(
                            privateCourseId
                        )

                        else -> {
                            Timber.e("FAIL: could not find course id")
                            finish()
                        }
                    }
                }
            }
            .addOnFailureListener(this) { t ->
                Timber.e("FAIL: getDynamicLink from intent : ${t.message}")
            }
    }

    private fun getCourseId(link: Uri, key: String): Int? {
        return link.getQueryParameter(key)?.toInt()
    }

    private inline fun <reified T : Activity> navigateToCourseDetail(courseId: Int) {
        Intent(this, T::class.java).apply {
            putExtra(EXTRA_FROM_DYNAMIC_LINK, courseId)
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
        const val EXTRA_FROM_DYNAMIC_LINK = "fromDynamicLink"
        private const val TOKEN_KEY_ACCESS = "access"
    }
}