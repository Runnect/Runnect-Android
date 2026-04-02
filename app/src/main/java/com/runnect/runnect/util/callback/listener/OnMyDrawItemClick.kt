package com.runnect.runnect.util.callback.listener

interface OnMyDrawItemClick {
    fun selectItem(id: Int, title: String) : Boolean
}