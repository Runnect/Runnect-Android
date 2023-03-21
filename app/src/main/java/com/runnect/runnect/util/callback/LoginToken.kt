package com.runnect.runnect.util.callback

interface LoginToken {
    fun setLoginToken(accessToken : String, refreshToken : String)
}