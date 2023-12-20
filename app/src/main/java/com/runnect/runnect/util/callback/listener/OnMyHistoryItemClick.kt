package com.runnect.runnect.util.callback.listener

import com.runnect.runnect.data.dto.HistoryInfoDTO

interface OnMyHistoryItemClick {
    fun selectItem(data: HistoryInfoDTO): Boolean
}