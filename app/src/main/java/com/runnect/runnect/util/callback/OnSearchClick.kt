package com.runnect.runnect.util.callback

import com.runnect.runnect.data.model.entity.SearchResultEntity

interface OnSearchClick {
    fun selectItem(item: SearchResultEntity)
}