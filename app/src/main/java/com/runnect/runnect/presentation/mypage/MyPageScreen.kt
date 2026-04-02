package com.runnect.runnect.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.G3
import com.runnect.runnect.presentation.ui.theme.G4
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.M3
import com.runnect.runnect.presentation.ui.theme.PretendardFontFamily

@Composable
fun MyPageScreen(
    state: MyPageUiState,
    onEditProfileClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onRewardClick: () -> Unit,
    onUploadClick: () -> Unit,
    onSettingClick: () -> Unit,
    onKakaoInquiryClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MyPageToolbar()

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = G3)
            }
        } else {
            ProfileSection(
                nickname = state.nickname,
                profileImgResId = state.profileImgResId,
                onEditClick = onEditProfileClick
            )
            LevelProgressSection(
                level = state.level,
                levelPercent = state.levelPercent
            )
            MenuSection(
                onHistoryClick = onHistoryClick,
                onRewardClick = onRewardClick,
                onUploadClick = onUploadClick,
                onSettingClick = onSettingClick,
                onKakaoInquiryClick = onKakaoInquiryClick
            )
            VersionSection()
        }
    }
}

@Composable
private fun MyPageToolbar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = stringResource(R.string.my_page_title),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = G1
        )
    }
}

@Composable
private fun ProfileSection(
    nickname: String,
    profileImgResId: Int,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .padding(horizontal = 23.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = profileImgResId,
            contentDescription = null,
            modifier = Modifier.size(63.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = nickname,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = M1
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onEditClick)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_mypage_nickname_edit),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.my_page_edit),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = M1
            )
        }
    }
}

@Composable
private fun LevelProgressSection(
    level: String,
    levelPercent: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(M3.copy(alpha = 0.6f))
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Row {
            Text(
                text = stringResource(R.string.my_page_lv_indicator),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = G1
            )
            Text(
                text = level,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = G1
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { levelPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(11.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = M1,
            trackColor = G4,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = levelPercent.toString(),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = G1
            )
            Text(
                text = stringResource(R.string.my_page_progress_max),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = G2
            )
        }
    }
}

@Composable
private fun MenuSection(
    onHistoryClick: () -> Unit,
    onRewardClick: () -> Unit,
    onUploadClick: () -> Unit,
    onSettingClick: () -> Unit,
    onKakaoInquiryClick: () -> Unit,
) {
    Column {
        MenuItem(
            title = stringResource(R.string.my_page_history_title),
            onClick = onHistoryClick
        )
        MenuItem(
            title = stringResource(R.string.my_page_reward_title),
            onClick = onRewardClick
        )
        MenuItem(
            title = stringResource(R.string.my_page_upload_title),
            onClick = onUploadClick
        )
        MenuItem(
            title = stringResource(R.string.my_page_setting_title),
            onClick = onSettingClick
        )
        MenuItem(
            title = stringResource(R.string.my_page_kakao_channel_inquiry),
            onClick = onKakaoInquiryClick,
            showDivider = false
        )
    }
}

@Composable
private fun MenuItem(
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.all_star),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = G1
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.all_front_arrow),
                contentDescription = null,
                modifier = Modifier.padding(end = 9.dp)
            )
        }
        if (showDivider) {
            HorizontalDivider(color = G4, thickness = 1.dp)
        }
    }
}

@Composable
private fun VersionSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.my_page_version_title),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = G2
        )
        Text(
            text = stringResource(R.string.my_page_version),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = G2
        )
    }
}
