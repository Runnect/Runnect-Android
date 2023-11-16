package com.runnect.runnect.util.mode

sealed class ScreenMode {
    object ReadOnlyMode: ScreenMode()
    object EditMode: ScreenMode()
}
