package com.runnect.runnect.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.databinding.ItemSearchBinding
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnSearchItemClick

class SearchAdapter(
    private val onSearchItemClick: OnSearchItemClick
) : ListAdapter<SearchResultEntity, SearchAdapter.SearchResultItemViewHolder>(diffUtil) {
    inner class SearchResultItemViewHolder(
        private val binding: ItemSearchBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: SearchResultEntity) {
            binding.searchResultEntity = data
        } //여기도 dto 구독해서 ui 바인딩 하는 목적이라 낭비임.

        fun bindViews(data: SearchResultEntity) {
            binding.root.setOnClickListener {
                onSearchItemClick.selectItem(data) //data는 넣었고 구체적인 동작은 오버라이드할 때
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        val view = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        holder.bindData(currentList[position])
        holder.bindViews(currentList[position])
    }

    companion object {
        private val diffUtil = ItemDiffCallback<SearchResultEntity>(
            onItemsTheSame = { old, new -> old.name == new.name },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}
