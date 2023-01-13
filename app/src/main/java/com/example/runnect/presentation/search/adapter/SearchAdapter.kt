package com.example.runnect.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.runnect.data.model.entity.SearchResultEntity
import com.example.runnect.databinding.ItemSearchBinding

class SearchAdapter(searchResultClickListener: (SearchResultEntity) -> Unit) :
    ListAdapter<SearchResultEntity, SearchAdapter.SearchResultItemViewHolder>(Differ()) {

    private val listener = searchResultClickListener

    inner class SearchResultItemViewHolder(
        private val binding: ItemSearchBinding, //뷰바인딩
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: SearchResultEntity) {
            binding.searchResultEntity = data
        }

        //클릭 이벤트 구현부
        fun bindViews(data: SearchResultEntity) {
            binding.root.setOnClickListener {
                listener(data)
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

    class Differ : DiffUtil.ItemCallback<SearchResultEntity>() {
        override fun areItemsTheSame(
            oldItem: SearchResultEntity,
            newItem: SearchResultEntity,
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: SearchResultEntity,
            newItem: SearchResultEntity,
        ): Boolean {
            return oldItem == newItem
        }

    }


}
