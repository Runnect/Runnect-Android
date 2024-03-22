package com.runnect.runnect.domain.common

class RunnectException(
    val code: Int,
    override val message: String?
) : Throwable(message) {

    fun toLog() = "$message(${code})"
}