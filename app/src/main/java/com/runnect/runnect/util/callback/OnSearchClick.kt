package com.runnect.runnect.util.callback

import com.runnect.runnect.data.dto.SearchResultEntity

interface OnSearchClick {
    fun startSearchLocation(item: SearchResultEntity)
}