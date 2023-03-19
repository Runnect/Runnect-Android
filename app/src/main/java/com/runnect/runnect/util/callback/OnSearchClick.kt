package com.runnect.runnect.util.callback

import com.runnect.runnect.data.model.SearchResultEntity

interface OnSearchClick {
    fun selectItem(item: SearchResultEntity)
}