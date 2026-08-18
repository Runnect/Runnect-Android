package com.runnect.runnect.presentation.run

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.PretendardFontFamily
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import kotlin.math.roundToInt

/**
 * 러닝 화면 상단 정보 바의 실시간 페이스(최근 15초 구간 기준) 표시.
 * 아직 측정할 만큼 움직이지 않았으면(null) "-"로 표시해 레이아웃이 흔들리지 않게 한다.
 */
@Composable
fun RunPaceStat(paceSecPerKm: Double?, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = "페이스",
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = G2,
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = formatPaceSecPerKm(paceSecPerKm),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = G1,
        )
        if (paceSecPerKm != null) {
            Text(
                modifier = Modifier.padding(start = 2.dp),
                text = "/km",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = G2,
            )
        }
    }
}

private fun formatPaceSecPerKm(paceSecPerKm: Double?): String {
    if (paceSecPerKm == null || paceSecPerKm.isNaN() || paceSecPerKm.isInfinite()) return "-"
    val totalSec = paceSecPerKm.roundToInt()
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d'%02d\"".format(min, sec)
}

@Preview(showBackground = true)
@Composable
private fun RunPaceStatPreview() {
    RunnectTheme {
        RunPaceStat(paceSecPerKm = 330.0)
    }
}

@Preview(showBackground = true)
@Composable
private fun RunPaceStatEmptyPreview() {
    RunnectTheme {
        RunPaceStat(paceSecPerKm = null)
    }
}
