package com.runnect.runnect.presentation.detail.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnect.runnect.domain.entity.CourseRanking
import com.runnect.runnect.domain.entity.CourseRankingEntry
import com.runnect.runnect.domain.entity.MyCourseRanking
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.G4
import com.runnect.runnect.presentation.ui.theme.G5
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.M3
import com.runnect.runnect.presentation.ui.theme.PretendardFontFamily
import com.runnect.runnect.presentation.ui.theme.RunnectTheme

private val GoldBg = Color(0xFFFBEFD4)
private val Gold = Color(0xFFC9971F)
private val SilverBg = Color(0xFFEEEFF1)
private val Silver = Color(0xFF8A8F98)
private val BronzeBg = Color(0xFFF3E3D3)
private val Bronze = Color(0xFFB0703B)

/**
 * 코스 상세 화면에 추가되는 기록 랭킹 섹션.
 * ranking이 null이거나 entries가 비어 있으면(로딩 중이거나 완주자가 없으면) 아무것도 그리지 않는다 —
 * 배지/섹션 노출 여부를 데이터 유무로만 결정해서 화면 쪽에 별도 분기를 두지 않기 위함.
 */
@Composable
fun RecordRankingSection(
    ranking: CourseRanking?,
    myRanking: MyCourseRanking?,
    onUserClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (ranking == null || ranking.entries.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(G5)
        )

        Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 18.dp)) {
            Text(
                text = "🏅 이 코스 기록 랭킹",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = G1,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "완주자 ${ranking.totalCount}명",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.5.sp,
                color = G2,
            )
            Spacer(modifier = Modifier.height(12.dp))

            ranking.entries.forEach { entry ->
                RankingRow(entry, onClick = { onUserClick(entry.userId) })
            }

            if (myRanking != null && myRanking.hasRecord) {
                Spacer(modifier = Modifier.height(6.dp))
                MyRankingRow(myRanking, onClick = { onUserClick(myRanking.userId) })
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "기록 갱신 시 랭킹이 즉시 반영돼요",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 10.5.sp,
                color = G2,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun RankingRow(entry: CourseRankingEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RankBadge(rank = entry.rank)
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(G4)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = entry.nickname,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.5.sp,
            color = G1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        RankingNumbers(time = entry.time, pace = entry.pace, color = G1)
    }
}

@Composable
private fun MyRankingRow(myRanking: MyCourseRanking, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(M3)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        myRanking.rank?.let {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$it",
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = M1,
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(G4)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "나",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                color = M1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(M1)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "PB",
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp,
                    color = Color.White,
                )
            }
        }
        RankingNumbers(time = myRanking.time.orEmpty(), pace = myRanking.pace.orEmpty(), color = M1)
    }
}

@Composable
private fun RankingNumbers(time: String, pace: String, color: Color) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = time,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            color = color,
        )
        Text(
            text = pace,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.5.sp,
            color = G2,
        )
    }
}

@Composable
private fun RankBadge(rank: Int) {
    val (bg, fg) = when (rank) {
        1 -> GoldBg to Gold
        2 -> SilverBg to Silver
        3 -> BronzeBg to Bronze
        else -> G4 to G2
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$rank",
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp,
            color = fg,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordRankingSectionPreview() {
    RunnectTheme {
        RecordRankingSection(
            ranking = CourseRanking(
                totalCount = 128,
                entries = listOf(
                    CourseRankingEntry(1, 1, "런너_지훈", 1, "11:24", "4'58\"/km"),
                    CourseRankingEntry(2, 2, "soo_running", 2, "11:47", "5'07\"/km"),
                    CourseRankingEntry(3, 3, "이번엔완주", 3, "12:02", "5'14\"/km"),
                    CourseRankingEntry(4, 4, "한강러너", 4, "12:31", "5'26\"/km"),
                )
            ),
            myRanking = MyCourseRanking(
                hasRecord = true,
                rank = 14,
                userId = 57,
                nickname = "말랑콩떡",
                time = "14:52",
                pace = "6'28\"/km",
            ),
            onUserClick = {},
        )
    }
}
