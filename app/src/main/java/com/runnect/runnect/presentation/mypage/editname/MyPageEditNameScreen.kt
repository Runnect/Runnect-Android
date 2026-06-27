package com.runnect.runnect.presentation.mypage.editname

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G3
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.M2
import com.runnect.runnect.presentation.ui.theme.RunnectTheme

@Composable
fun MyPageEditNameScreen(
    state: EditNameUiState,
    onBackClick: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize()) {
            EditNameToolbar(
                onBackClick = onBackClick,
                onSubmitClick = onSubmitClick,
                submitEnabled = state.nickname.isNotEmpty(),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(147.dp))
                AsyncImage(
                    model = state.profileImgResId,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                )
                Spacer(modifier = Modifier.height(48.dp))
                NicknameTextField(
                    value = state.nickname,
                    onValueChange = onNicknameChange,
                    onDone = { focusManager.clearFocus() },
                )
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                color = G3,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun EditNameToolbar(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    submitEnabled: Boolean,
) {
    val textStyle = RunnectTheme.textStyle
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.all_back_arrow),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onBackClick),
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = stringResource(R.string.my_page_edit_name_title),
            style = textStyle.bold17.copy(fontSize = 18.sp),
            color = G1,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.my_page_edit_name_finish),
            style = textStyle.bold15.copy(fontSize = 16.sp),
            color = if (submitEnabled) M1 else G3,
            modifier = Modifier
                .clickable(enabled = submitEnabled, onClick = onSubmitClick)
                .padding(8.dp),
        )
    }
}

@Composable
private fun NicknameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
) {
    val textStyle = RunnectTheme.textStyle
    // OutlinedTextField enforces a 56dp minimum height (M3 internal contentPadding),
    // which clips text inside the 44dp height the XML version used.
    // BasicTextField + decorationBox gives identical layout to the original AppCompatEditText.
    BasicTextField(
        value = value,
        onValueChange = { if (it.length <= 7) onValueChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        textStyle = textStyle.semiBold15.copy(textAlign = TextAlign.Center, color = G1),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(width = 1.dp, color = M2, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.my_page_edit_name_guide),
                        style = textStyle.semiBold15.copy(textAlign = TextAlign.Center),
                        color = G3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                innerTextField()
            }
        },
    )
}
