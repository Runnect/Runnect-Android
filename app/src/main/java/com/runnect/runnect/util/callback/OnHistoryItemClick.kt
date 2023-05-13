package com.runnect.runnect.util.callback

import com.runnect.runnect.data.dto.HistoryInfoDTO

interface OnHistoryItemClick {
    fun selectItem(data: HistoryInfoDTO): Boolean
}