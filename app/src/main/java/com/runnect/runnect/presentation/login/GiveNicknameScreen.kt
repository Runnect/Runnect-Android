package com.runnect.runnect.presentation.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.runnect.runnect.R
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G3
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.presentation.ui.theme.White

object GiveNicknameScreenTestTags {
    const val NICKNAME_INPUT = "give_nickname_input"
    const val START_BUTTON = "give_nickname_start_button"
    const val LOADING_INDICATOR = "give_nickname_loading_indicator"
}

data class GiveNicknameUiState(
    val nickName: String = "",
    val isLoading: Boolean = false,
    val isStartEnabled: Boolean = false,
) {
    companion object {
        fun from(
            nickName: String?,
            uiState: UiState,
        ) = GiveNicknameUiState(
            nickName = nickName.orEmpty(),
            isLoading = uiState is UiState.Loading,
            isStartEnabled = !nickName.isNullOrBlank() && uiState !is UiState.Loading
        )
    }
}

@Composable
fun GiveNicknameScreen(
    state: GiveNicknameUiState,
    onNickNameChange: (String) -> Unit,
    onStartClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(top = 83.dp)
        ) {
            GiveNicknameTitle()
            Spacer(modifier = Modifier.height(90.dp))
            Image(
                painter = painterResource(R.drawable.user_profile_basic),
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(48.dp))
            NicknameTextField(
                nickName = state.nickName,
                onNickNameChange = onNickNameChange,
                onDone = { focusManager.clearFocus() }
            )
        }
        StartButton(
            enabled = state.isStartEnabled,
            onClick = onStartClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 8.dp)
        )
        if (state.isLoading) {
            CircularProgressIndicator(
                color = G3,
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag(GiveNicknameScreenTestTags.LOADING_INDICATOR)
            )
        }
    }
}

@Composable
private fun GiveNicknameTitle() {
    Column {
        Row {
            Text(
                text = stringResource(R.string.give_nickname_title_1),
                style = RunnectTheme.textStyle.bold22,
                color = M1
            )
            Text(
                text = stringResource(R.string.give_nickname_title_2),
                style = RunnectTheme.textStyle.medium15.copy(fontSize = RunnectTheme.textStyle.bold22.fontSize),
                color = G1
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.give_nickname_title_3),
            style = RunnectTheme.textStyle.medium15.copy(fontSize = RunnectTheme.textStyle.bold22.fontSize),
            color = G1
        )
    }
}

@Composable
private fun NicknameTextField(
    nickName: String,
    onNickNameChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    BasicTextField(
        value = nickName,
        onValueChange = { nextValue ->
            if (nextValue.length <= NICKNAME_MAX_LENGTH) onNickNameChange(nextValue)
        },
        modifier = Modifier.testTag(GiveNicknameScreenTestTags.NICKNAME_INPUT),
        singleLine = true,
        textStyle = RunnectTheme.textStyle.medium15.copy(
            color = G1,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(M1),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(
                        border = BorderStroke(1.dp, G3),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                if (nickName.isEmpty()) {
                    Text(
                        text = stringResource(R.string.give_nickname_edit_input),
                        style = RunnectTheme.textStyle.medium15,
                        color = G3,
                        textAlign = TextAlign.Center
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun StartButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .testTag(GiveNicknameScreenTestTags.START_BUTTON)
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = if (enabled) M1 else G3,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Text(
            text = stringResource(R.string.give_nickname_finish),
            style = RunnectTheme.textStyle.semiBold15,
            color = White
        )
    }
}

private const val NICKNAME_MAX_LENGTH = 7
