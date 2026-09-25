package com.runnect.runnect.presentation.run

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.presentation.ui.theme.White

object RunAlertBannerTestTags {
    const val BANNER = "run_alert_banner"
}

private val WarnYellow = Color(0xFFFFC617)
private val WarnInk = Color(0xFF4A3800)
private val WarnInkSoft = Color(0xFF6B5300)
private val DangerRed = Color(0xFFFF473A)

/** 러닝 화면 상단 바 아래에 뜨는 코스 이탈 / 페이스 저하 알림. alert가 null이면 사라진다. */
@Composable
fun RunAlertBanner(alert: RunAlert?, modifier: Modifier = Modifier) {
    // 사라지는 애니메이션 동안 마지막 내용을 유지하기 위해 null이 아닌 마지막 값을 기억한다.
    val lastAlert = remember { LastValueHolder<RunAlert>() }
    alert?.let { lastAlert.value = it }

    AnimatedVisibility(
        visible = alert != null,
        modifier = modifier,
        enter = slideInVertically { -it / 2 } + fadeIn(),
        exit = slideOutVertically { -it / 2 } + fadeOut(),
    ) {
        lastAlert.value?.let { RunAlertContent(it) }
    }
}

@Composable
private fun RunAlertContent(alert: RunAlert) {
    val (background, titleColor, descColor) = when (alert) {
        is RunAlert.OffRoute -> Triple(DangerRed, White, White.copy(alpha = 0.85f))
        is RunAlert.PaceDrop -> Triple(WarnYellow, WarnInk, WarnInkSoft)
    }
    val title = when (alert) {
        is RunAlert.OffRoute -> stringResource(R.string.run_alert_off_route_title)
        is RunAlert.PaceDrop -> stringResource(R.string.run_alert_pace_drop_title)
    }
    val description = when (alert) {
        is RunAlert.OffRoute -> stringResource(R.string.run_alert_off_route_desc, alert.distanceM)
        is RunAlert.PaceDrop -> stringResource(
            R.string.run_alert_pace_drop_desc,
            PaceFormat.format(alert.targetSecPerKm),
            PaceFormat.format(alert.currentSecPerKm),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(background, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .testTag(RunAlertBannerTestTags.BANNER),
    ) {
        Text(text = title, style = RunnectTheme.textStyle.semiBold15, color = titleColor)
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = description,
            style = RunnectTheme.textStyle.medium12,
            color = descColor,
        )
    }
}

private class LastValueHolder<T> {
    var value: T? = null
}

@Preview(showBackground = true)
@Composable
private fun RunAlertBannerOffRoutePreview() {
    RunnectTheme { RunAlertBanner(alert = RunAlert.OffRoute(distanceM = 45)) }
}

@Preview(showBackground = true)
@Composable
private fun RunAlertBannerPaceDropPreview() {
    RunnectTheme { RunAlertBanner(alert = RunAlert.PaceDrop(targetSecPerKm = 330.0, currentSecPerKm = 412.0)) }
}
