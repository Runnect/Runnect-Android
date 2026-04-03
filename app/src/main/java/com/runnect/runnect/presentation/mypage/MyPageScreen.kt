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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.G3
import com.runnect.runnect.presentation.ui.theme.G4
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.M3
import com.runnect.runnect.presentation.ui.theme.RunnectTheme

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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MyPageToolbar()

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = G3)
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
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
    }
}

@Composable
private fun MyPageToolbar() {
    val textStyle = RunnectTheme.textStyle
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = stringResource(R.string.my_page_title),
            style = textStyle.bold20,
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
    val textStyle = RunnectTheme.textStyle
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
            style = textStyle.bold17,
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
                style = textStyle.medium12,
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
    val textStyle = RunnectTheme.textStyle
    val clampedPercent = levelPercent.coerceIn(0, 100)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(M3.copy(alpha = 0.6f))
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Row {
            Text(
                text = stringResource(R.string.my_page_lv_indicator),
                style = textStyle.bold15,
                color = G1
            )
            Text(
                text = level,
                style = textStyle.bold15,
                color = G1
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { clampedPercent / 100f },
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
                text = clampedPercent.toString(),
                style = textStyle.semiBold13,
                color = G1
            )
            Text(
                text = stringResource(R.string.my_page_progress_max),
                style = textStyle.semiBold13,
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
    val textStyle = RunnectTheme.textStyle
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
                style = textStyle.medium15,
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
    val textStyle = RunnectTheme.textStyle
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
            style = textStyle.medium15,
            color = G2
        )
        Text(
            text = stringResource(R.string.my_page_version),
            style = textStyle.regular14,
            color = G2
        )
    }
}
