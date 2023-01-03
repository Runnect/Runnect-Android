package com.runnect.runnect.util

import androidx.recyclerview.widget.DiffUtil

interface DiffUtilEquals{
    fun <T> equals(oldItem:T, newItem:T):Boolean?
}

class DiffUtilItemCallback<T> :DiffUtil.ItemCallback<T>(), DiffUtilEquals {

    override fun <T> equals(olditem: T, newItem: T): Boolean {
        return olditem!! == newItem
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem:T, newItem: T): Boolean {
        return equals(oldItem,newItem)
    }
}