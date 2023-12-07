package com.runnect.runnect.util

import androidx.annotation.DrawableRes

sealed class RippleEffect {

    object Ripple : RippleEffect()

    object CircleRipple: RippleEffect()

    data class CustomRipple(
        @DrawableRes val rippleEffectResId: Int = 0
    ) : RippleEffect()
}
