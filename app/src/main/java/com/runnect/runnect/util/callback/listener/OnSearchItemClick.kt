package com.runnect.runnect.util.callback.listener

import com.runnect.runnect.data.dto.SearchResultEntity

interface OnSearchItemClick {
    fun selectItem(item: SearchResultEntity)
}