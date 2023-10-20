package com.runnect.runnect.presentation.mypage.history.detail

sealed class ScreenMode {
    object ReadOnlyMode: ScreenMode()
    object EditMode: ScreenMode()
}
