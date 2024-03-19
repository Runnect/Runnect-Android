package com.runnect.runnect.presentation.scheme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
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

        handleDynamicLinks()
    }

    private fun handleDynamicLinks() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                val deeplink = pendingDynamicLinkData.link
                if (deeplink != null) {
                    val publicCourseId = deeplink.getQueryParameter(KEY_PUBLIC_COURSE_ID)?.toInt() ?: -1
                    if (publicCourseId != -1) {
                        navigateTo<CourseDetailActivity>(publicCourseId)
                        return@addOnSuccessListener
                    }

                    val privateCourseId = deeplink.getQueryParameter(KEY_PRIVATE_COURSE_ID)?.toInt() ?: -1
                    if (privateCourseId != -1) {
                        navigateTo<MyDrawDetailActivity>(privateCourseId)
                        return@addOnSuccessListener
                    }
                }

                Timber.d("not from dynamic link")
            }
            .addOnFailureListener(this) { t ->
                Timber.e("getDynamicLink from intent is fail : ${t.message}")
            }
    }

    private inline fun <reified T : Activity> navigateTo(courseId: Int) {
        Intent(this, T::class.java).apply {
            putExtra(EXTRA_FROM_DYNAMIC_LINK, courseId)
            startActivity(this)
        }
    }

    companion object {
        const val EXTRA_FROM_DYNAMIC_LINK = "fromDynamicLink"
    }
}