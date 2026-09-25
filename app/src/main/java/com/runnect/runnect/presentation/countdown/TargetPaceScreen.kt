package com.runnect.runnect.presentation.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runnect.runnect.R
import com.runnect.runnect.presentation.run.PaceFormat
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.G4
import com.runnect.runnect.presentation.ui.theme.G5
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.presentation.ui.theme.White

object TargetPaceScreenTestTags {
    const val START_BUTTON = "target_pace_start_button"
    const val CUSTOM_VALUE = "target_pace_custom_value"
    fun option(option: TargetPaceOption) = "target_pace_option_${option.name}"
}

/** 카운트다운 전에 목표 페이스를 고르는 화면. 목표 페이스는 러닝 중 페이스 저하 알림 기준으로만 쓰인다. */
@Composable
fun TargetPaceScreen(
    uiState: TargetPaceUiState,
    onSelect: (TargetPaceOption) -> Unit,
    onAdjustCustomPace: (Int) -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.target_pace_title),
            style = RunnectTheme.textStyle.bold20,
            color = G1,
        )
        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = stringResource(R.string.target_pace_subtitle),
            style = RunnectTheme.textStyle.regular14,
            color = G2,
        )
        Spacer(modifier = Modifier.height(24.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PaceChip(TargetPaceOption.NONE, stringResource(R.string.target_pace_none), null, uiState, onSelect)
            uiState.topRankPaceSecPerKm?.let {
                PaceChip(TargetPaceOption.TOP_RANK, stringResource(R.string.target_pace_top_rank), it, uiState, onSelect)
            }
            uiState.myBestPaceSecPerKm?.let {
                PaceChip(TargetPaceOption.MY_BEST, stringResource(R.string.target_pace_my_best), it, uiState, onSelect)
            }
            PaceChip(TargetPaceOption.CUSTOM, stringResource(R.string.target_pace_custom), null, uiState, onSelect)
        }

        if (uiState.selection == TargetPaceOption.CUSTOM) {
            CustomPaceStepper(
                paceSecPerKm = uiState.customPaceSecPerKm,
                onAdjust = onAdjustCustomPace,
                modifier = Modifier.padding(top = 20.dp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(M1)
                .clickable(role = Role.Button, onClick = onStart)
                .testTag(TargetPaceScreenTestTags.START_BUTTON),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.target_pace_start),
                style = RunnectTheme.textStyle.bold17,
                color = White,
            )
        }
    }
}

@Composable
private fun PaceChip(
    option: TargetPaceOption,
    label: String,
    paceSecPerKm: Double?,
    uiState: TargetPaceUiState,
    onSelect: (TargetPaceOption) -> Unit,
) {
    val isSelected = uiState.selection == option
    val text = if (paceSecPerKm != null) "$label ${PaceFormat.format(paceSecPerKm)}/km" else label
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) M1 else White)
            .border(1.dp, if (isSelected) M1 else G4, CircleShape)
            .clickable(role = Role.RadioButton) { onSelect(option) }
            .semantics { selected = isSelected }
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .testTag(TargetPaceScreenTestTags.option(option)),
    ) {
        Text(text = text, style = RunnectTheme.textStyle.semiBold13, color = if (isSelected) White else G1)
    }
}

@Composable
private fun CustomPaceStepper(paceSecPerKm: Double, onAdjust: (Int) -> Unit, modifier: Modifier = Modifier) {
    val step = TargetPaceViewModel.CUSTOM_PACE_STEP_SEC
    val decreaseDesc = stringResource(R.string.target_pace_decrease)
    val increaseDesc = stringResource(R.string.target_pace_increase)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(G5)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(text = "−", description = decreaseDesc) { onAdjust(-step) }
        Text(
            modifier = Modifier
                .weight(1f)
                .testTag(TargetPaceScreenTestTags.CUSTOM_VALUE),
            text = "${PaceFormat.format(paceSecPerKm)}/km",
            style = RunnectTheme.textStyle.bold22,
            color = G1,
            textAlign = TextAlign.Center,
        )
        StepperButton(text = "+", description = increaseDesc) { onAdjust(step) }
    }
}

@Composable
private fun StepperButton(text: String, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(White)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = RunnectTheme.textStyle.bold20, color = M1)
    }
}

@Preview(showBackground = true)
@Composable
private fun TargetPaceScreenPreview() {
    RunnectTheme {
        TargetPaceScreen(
            uiState = TargetPaceUiState(
                topRankPaceSecPerKm = 298.0,
                myBestPaceSecPerKm = 388.0,
                selection = TargetPaceOption.CUSTOM,
            ),
            onSelect = {},
            onAdjustCustomPace = {},
            onStart = {},
        )
    }
}
