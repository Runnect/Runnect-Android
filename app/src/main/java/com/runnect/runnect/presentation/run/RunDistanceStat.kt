package com.runnect.runnect.presentation.run

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.PretendardFontFamily
import com.runnect.runnect.presentation.ui.theme.RunnectTheme

/**
 * 러닝 화면 상단 정보 바의 실시간 이동 거리 표시.
 * 기존 tv_total_distance_content(값) + tv_total_distance_unit(단위) 두 XML TextView를
 * 대체한다 — 아이콘/라벨("거리")은 위치 고정값이라 XML에 그대로 둔다.
 */
@Composable
fun RunDistanceStat(distanceKm: Double, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = distanceKm.toString(),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = G1,
        )
        Text(
            text = "km",
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = G2,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RunDistanceStatPreview() {
    RunnectTheme {
        RunDistanceStat(distanceKm = 2.3)
    }
}
