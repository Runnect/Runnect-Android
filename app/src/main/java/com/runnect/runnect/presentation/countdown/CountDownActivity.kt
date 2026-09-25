package com.runnect.runnect.presentation.countdown

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.presentation.run.RunActivity
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.Param
import com.runnect.runnect.util.extension.getCompatibleParcelableExtra
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountDownActivity : AppCompatActivity() {
    private val courseData: CourseData? by lazy { intent.getCompatibleParcelableExtra(EXTRA_COURSE_DATA) }
    private val targetPaceViewModel: TargetPaceViewModel by viewModels()

    // 러닝 중 알림(타이머, 코스 이탈/페이스 저하 상단 알림)을 띄우려면 Android 13+에서 알림 권한이 필요하다.
    // 거절해도 러닝은 그대로 진행한다(진동 알림은 권한 없이도 울린다).
    private var onNotificationPermissionResult: (() -> Unit)? = null
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            onNotificationPermissionResult?.invoke()
            onNotificationPermissionResult = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics.logEvent(
            EventName.VIEW_COUNTDOWN,
            Param.COURSE_ID to courseData?.courseId
        )

        targetPaceViewModel.loadRecommendations(
            publicCourseId = courseData?.publicCourseId,
            distanceKm = courseData?.distance?.toDouble(),
        )

        setContent {
            RunnectTheme {
                // 목표 페이스를 고른 뒤에 카운트다운을 시작한다.
                var isPaceChosen by rememberSaveable { mutableStateOf(false) }
                if (isPaceChosen) {
                    CountDownRoute(
                        onFinished = ::moveToRun
                    )
                } else {
                    val uiState by targetPaceViewModel.uiState.collectAsStateWithLifecycle()
                    TargetPaceScreen(
                        uiState = uiState,
                        onSelect = targetPaceViewModel::select,
                        onAdjustCustomPace = targetPaceViewModel::adjustCustomPace,
                        onStart = { requestNotificationPermissionThen { isPaceChosen = true } },
                    )
                }
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

    private fun requestNotificationPermissionThen(onDone: () -> Unit) {
        val needsRequest = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (!needsRequest) {
            onDone()
            return
        }
        onNotificationPermissionResult = onDone
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun moveToRun() {
        val intentToRun = Intent(this, RunActivity::class.java)
        courseData?.let { courseData ->
            intentToRun.putExtra(EXTRA_COUNTDOWN_TO_RUN, courseData)
        }
        targetPaceViewModel.uiState.value.selectedPaceSecPerKm?.let {
            intentToRun.putExtra(RunActivity.EXTRA_TARGET_PACE_SEC_PER_KM, it)
        }
        startActivity(intentToRun)
        finish()
    }

    companion object {
        const val EXTRA_COUNTDOWN_TO_RUN = "CountToRunData"
        const val EXTRA_COURSE_DATA = "CourseData"
    }
}
