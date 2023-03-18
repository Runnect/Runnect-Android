package com.runnect.runnect.util.callback

import com.runnect.runnect.data.model.ResponseGetScrapDto

interface OnScrapCourseClick {
    fun selectItem(item: ResponseGetScrapDto.Data.Scrap)
}