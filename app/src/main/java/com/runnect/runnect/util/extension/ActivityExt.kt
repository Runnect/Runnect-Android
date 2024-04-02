package com.runnect.runnect.util.extension

import android.app.Activity
import android.content.Intent
import com.runnect.runnect.R

/**
 * 애니메이션과 함께 새로운 액티비티를 시작합니다.
 *
 * @param intentBuilder 새로 시작할 액티비티의 [Intent]
 * @param withFinish 현재 액티비티를 종료할 지 여부
 */
inline fun <reified T : Activity> Activity.startActivityWithAnimation(
    intentBuilder: Intent.() -> Intent = { this },
    withFinish: Boolean = false,
) {
    startActivity(intentBuilder(Intent(this, T::class.java)))
    applyScreenEnterAnimation()

    if (withFinish) finish()
}

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
