package com.runnect.runnect.util.callback.listener

import com.runnect.runnect.data.dto.MyScrapCourse

interface OnScrapItemClick {
    fun selectItem(item: MyScrapCourse)
}