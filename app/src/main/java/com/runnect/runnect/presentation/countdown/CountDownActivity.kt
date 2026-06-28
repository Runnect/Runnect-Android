package com.runnect.runnect.presentation.countdown

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.presentation.run.RunActivity
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.Param
import com.runnect.runnect.util.extension.getCompatibleParcelableExtra

class CountDownActivity : AppCompatActivity() {
    private val courseData: CourseData? by lazy { intent.getCompatibleParcelableExtra(EXTRA_COURSE_DATA) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics.logEvent(
            EventName.VIEW_COUNTDOWN,
            Param.COURSE_ID to courseData?.courseId
        )

        setContent {
            RunnectTheme {
                CountDownRoute(
                    onFinished = ::moveToRun
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Analytics.logEvent(
            EventName.CLICK_CANCEL_COUNTDOWN,
            Param.COURSE_ID to courseData?.courseId
        )
        finish()
        overridePendingTransition(
            com.runnect.runnect.R.anim.slide_in_left,
            com.runnect.runnect.R.anim.slide_out_right
        )
    }

    private fun moveToRun() {
        val intentToRun = Intent(this, RunActivity::class.java)
        courseData?.let { courseData ->
            intentToRun.putExtra(EXTRA_COUNTDOWN_TO_RUN, courseData)
        }
        startActivity(intentToRun)
        finish()
    }

    companion object {
        const val EXTRA_COUNTDOWN_TO_RUN = "CountToRunData"
        const val EXTRA_COURSE_DATA = "CourseData"
    }
}
