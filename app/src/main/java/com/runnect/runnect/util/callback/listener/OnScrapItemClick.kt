package com.runnect.runnect.util.callback.listener

import com.runnect.runnect.domain.entity.MyScrapCourse

interface OnScrapItemClick {
    fun selectItem(item: MyScrapCourse)
}