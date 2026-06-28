package com.runnect.runnect.presentation.countdown

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.presentation.ui.theme.White
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos

object CountDownScreenTestTags {
    const val BACKGROUND = "count_down_background"
    const val NUMBER = "count_down_number"
    const val DESCRIPTION = "count_down_description"
}

object CountDownStateMachine {
    const val INITIAL_COUNT = 3
    private const val LAST_VISIBLE_COUNT = 1
    const val TICK_MILLIS = 1_000L

    fun nextCount(currentCount: Int): Int? =
        if (currentCount > LAST_VISIBLE_COUNT) currentCount - 1 else null

    @DrawableRes
    fun numberDrawableRes(count: Int): Int = when (count) {
        3 -> R.drawable.anim_num3
        2 -> R.drawable.anim_num2
        1 -> R.drawable.anim_num1
        else -> error("Unsupported countdown number: $count")
    }
}

object CountDownAnimationSpec {
    const val INITIAL_SCALE = 0.4f
    const val TARGET_SCALE = 1f

    val AccelerateDecelerateEasing = Easing { fraction ->
        (cos((fraction + 1f) * PI).toFloat() / 2f) + 0.5f
    }
}

@Composable
fun CountDownRoute(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentCount by remember { mutableIntStateOf(CountDownStateMachine.INITIAL_COUNT) }
    var isFinished by remember { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (!isFinished) {
                delay(CountDownStateMachine.TICK_MILLIS)
                val nextCount = CountDownStateMachine.nextCount(currentCount)
                if (nextCount == null) {
                    isFinished = true
                    onFinished()
                } else {
                    currentCount = nextCount
                }
            }
        }
    }

    CountDownContent(
        count = currentCount,
        modifier = modifier
    )
}

@Composable
fun CountDownContent(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(CountDownAnimationSpec.INITIAL_SCALE) }

    LaunchedEffect(count) {
        scale.snapTo(CountDownAnimationSpec.INITIAL_SCALE)
        scale.animateTo(
            targetValue = CountDownAnimationSpec.TARGET_SCALE,
            animationSpec = tween(
                durationMillis = CountDownStateMachine.TICK_MILLIS.toInt(),
                easing = CountDownAnimationSpec.AccelerateDecelerateEasing
            )
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.star_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .testTag(CountDownScreenTestTags.BACKGROUND)
        )
        Image(
            painter = painterResource(CountDownStateMachine.numberDrawableRes(count)),
            contentDescription = count.toString(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-350).dp)
                .size(width = 88.dp, height = 117.dp)
                .scale(scale.value)
                .testTag(CountDownScreenTestTags.NUMBER)
        )
        Text(
            text = stringResource(R.string.count_down_desc),
            style = RunnectTheme.textStyle.medium15,
            color = White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-280).dp)
                .testTag(CountDownScreenTestTags.DESCRIPTION)
        )
    }
}
