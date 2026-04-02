package com.runnect.runnect.application

import android.content.Context

enum class ApiMode {
    NODE,
    TEST,
    JAVA;

    companion object {
        private fun asValue(mode: String): ApiMode = when(mode.uppercase()) {
            "NODE" -> NODE
            "JAVA" -> JAVA
            else -> TEST
        }

        fun getCurrentApiMode(context: Context): ApiMode {
            return asValue(
                PreferenceManager.getString(context, ApplicationClass.API_MODE) ?: ""
            )
        }
    }
}