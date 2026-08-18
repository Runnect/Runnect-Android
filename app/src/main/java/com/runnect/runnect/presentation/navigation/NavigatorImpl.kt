package com.runnect.runnect.presentation.navigation

import android.content.Context
import android.content.Intent
import com.runnect.runnect.presentation.MainActivity
import javax.inject.Inject

class NavigatorImpl @Inject constructor() : Navigator {

    override fun navigateToMain(context: Context, tab: MainTab?, mode: NavigationMode) {
        val intent = Intent(context, MainActivity::class.java).apply {
            tab?.let { putExtra(EXTRA_MAIN_TAB, it) }
            addFlags(mode.toIntentFlags())
        }
        context.startActivity(intent)
    }

    private fun NavigationMode.toIntentFlags(): Int = when (this) {
        NavigationMode.DEFAULT -> 0
        NavigationMode.CLEAR_TOP -> Intent.FLAG_ACTIVITY_CLEAR_TOP
        NavigationMode.NEW_TASK_CLEAR_TASK ->
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
