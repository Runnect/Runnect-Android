package com.runnect.runnect.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.runnect.runnect.R
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G3
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.presentation.ui.theme.White
import androidx.compose.ui.graphics.Color

object LoginScreenTestTags {
    const val GOOGLE_LOGIN_BUTTON = "google_login_button"
    const val KAKAO_LOGIN_BUTTON = "kakao_login_button"
    const val VISITOR_MODE_BUTTON = "visitor_mode_button"
    const val LOADING_INDICATOR = "login_loading_indicator"
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        fun from(
            loginState: UiState,
            errorMessage: String?,
        ) = LoginUiState(
            isLoading = loginState is UiState.Loading,
            errorMessage = errorMessage
        )
    }
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    onGoogleLoginClick: () -> Unit,
    onKakaoLoginClick: () -> Unit,
    onVisitorModeClick: () -> Unit,
    onErrorShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onErrorShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(R.drawable.splash),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            LoginActions(
                onGoogleLoginClick = onGoogleLoginClick,
                onKakaoLoginClick = onKakaoLoginClick,
                onVisitorModeClick = onVisitorModeClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = G3,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag(LoginScreenTestTags.LOADING_INDICATOR)
                )
            }
        }
    }
}

@Composable
private fun LoginActions(
    onGoogleLoginClick: () -> Unit,
    onKakaoLoginClick: () -> Unit,
    onVisitorModeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 39.dp)
    ) {
        SocialLoginButton(
            text = "구글로 로그인",
            iconRes = R.drawable.ic_google_login,
            backgroundColor = White,
            testTag = LoginScreenTestTags.GOOGLE_LOGIN_BUTTON,
            onClick = onGoogleLoginClick
        )
        Spacer(modifier = Modifier.height(8.dp))
        SocialLoginButton(
            text = "카카오로 로그인",
            iconRes = R.drawable.ic_kakao_login,
            backgroundColor = Color(0xFFFEE500),
            testTag = LoginScreenTestTags.KAKAO_LOGIN_BUTTON,
            onClick = onKakaoLoginClick
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.login_visitor_mode),
            style = RunnectTheme.textStyle.medium15.copy(textDecoration = TextDecoration.Underline),
            color = White,
            modifier = Modifier
                .testTag(LoginScreenTestTags.VISITOR_MODE_BUTTON)
                .clickable(onClick = onVisitorModeClick)
        )
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    iconRes: Int,
    backgroundColor: Color,
    testTag: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = text,
            style = RunnectTheme.textStyle.medium15,
            color = G1,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
