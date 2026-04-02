package com.runnect.runnect.util.extension

import android.app.Activity
import com.runnect.runnect.R

fun Activity.navigateToPreviousScreenWithAnimation() {
    finish()
    applyScreenExitAnimation()
}

fun Activity.applyScreenExitAnimation() {
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}

fun Activity.applyScreenEnterAnimation() {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}
