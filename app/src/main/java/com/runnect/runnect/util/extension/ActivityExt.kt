package com.runnect.runnect.util.extension

import android.app.Activity
import com.runnect.runnect.R

fun Activity.navigateToPreviousScreenWithAnimation() {
    finish()
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}