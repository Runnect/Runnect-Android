package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverRecommendHeaderBinding

class RecommendHeaderAdapter: RecyclerView.Adapter<RecommendHeaderAdapter.RecommendHeaderViewHolder>() {
    class RecommendHeaderViewHolder(
        binding: ItemDiscoverRecommendHeaderBinding
    ): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendHeaderViewHolder {
        return RecommendHeaderViewHolder(
            ItemDiscoverRecommendHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecommendHeaderViewHolder, position: Int) {}
}
