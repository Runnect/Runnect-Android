package com.runnect.runnect.presentation.run

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.G3
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.PretendardFontFamily
import com.runnect.runnect.presentation.ui.theme.RunnectTheme

object RunStatsPanelTestTags {
    const val DISTANCE = "run_stats_distance"
    const val TIME = "run_stats_time"
    const val PACE = "run_stats_pace"
    const val TARGET = "run_stats_target"
}

/**
 * 러닝 화면 상단의 거리 / 시간 / 페이스 3칸. 세 칸 모두 같은 크기의 라벨-값-단위 구조로 맞춰 한눈에 읽히게 하고,
 * 칸은 내용 폭만큼만 차지한 채 칸 사이 간격을 똑같이 나눈다(SpaceBetween) — 칸 폭을 비율로 나누면 내용 길이가 달라
 * 보이는 간격이 제각각이 된다. 숫자는 고정폭(tnum)이라 값이 바뀌어도 간격이 흔들리지 않는다.
 * 목표 페이스는 페이스 칸 아래 고정 자리에 둬서(목표가 없으면 "목표 없음") 값이 바뀌어도 배치가 흔들리지 않게 한다.
 */
@Composable
fun RunStatsPanel(state: RunTrackingState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatColumn(
            label = stringResource(R.string.run_distance_title),
            value = "%.1f".format(state.distanceKm),
            unit = stringResource(R.string.run_distance_unit),
            valueTag = RunStatsPanelTestTags.DISTANCE,
        )
        StatColumn(
            label = stringResource(R.string.run_time_title),
            value = TimerService.formatElapsed(state.elapsedSec),
            unit = null,
            valueTag = RunStatsPanelTestTags.TIME,
        )
        StatColumn(
            label = stringResource(R.string.run_pace_title),
            value = PaceFormat.format(state.paceSecPerKm),
            unit = state.paceSecPerKm?.let { stringResource(R.string.run_pace_unit) },
            valueTag = RunStatsPanelTestTags.PACE,
        ) {
            val target = state.targetPaceSecPerKm
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .testTag(RunStatsPanelTestTags.TARGET),
                text = if (target != null) {
                    stringResource(R.string.run_pace_target, PaceFormat.format(target))
                } else {
                    stringResource(R.string.run_pace_target_none)
                },
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (target != null) M1 else G3,
            )
        }
    }
}

@Composable
private fun StatColumn(
    label: String,
    value: String,
    unit: String?,
    valueTag: String,
    modifier: Modifier = Modifier,
    footer: @Composable () -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = G2,
        )
        Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.Bottom) {
            Text(
                modifier = Modifier.alignByBaseline().testTag(valueTag),
                text = value,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                style = TextStyle(fontFeatureSettings = "tnum"),
                color = G1,
                maxLines = 1,
                overflow = TextOverflow.Visible,
            )
            if (unit != null) {
                Text(
                    modifier = Modifier
                        .alignByBaseline()
                        .padding(start = 2.dp),
                    text = unit,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = G2,
                    maxLines = 1,
                )
            }
        }
        footer()
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RunStatsPanelPreview() {
    RunnectTheme {
        RunStatsPanel(
            RunTrackingState(elapsedSec = 41, distanceM = 100.0, paceSecPerKm = 334.0, targetPaceSecPerKm = 330.0)
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RunStatsPanelNoTargetPreview() {
    RunnectTheme {
        RunStatsPanel(RunTrackingState(elapsedSec = 13))
    }
}
