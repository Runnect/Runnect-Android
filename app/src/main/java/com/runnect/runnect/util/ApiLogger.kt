package com.runnect.runnect.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class ApiLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        val logName = "Retrofit2 Logger"
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val jsonElement = JsonParser.parseString(message)
                val prettyPrintJson = GsonBuilder().setPrettyPrinting().create().toJson(jsonElement)

                Timber.tag(logName).v(prettyPrintJson)
            } catch (e: JsonSyntaxException) {
                Timber.tag(logName).v(message)
            }
        } else {
            Timber.tag(logName).v(message)
        }
    }
}