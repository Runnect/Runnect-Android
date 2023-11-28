package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverScrollBinding

class DiscoverScrollAdapter :
    RecyclerView.Adapter<DiscoverScrollAdapter.DiscoverScrollViewHolder>() {
    var binding: ItemDiscoverScrollBinding

    class DiscoverScrollViewHolder(
        binding: ItemDiscoverScrollBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverScrollViewHolder {
        binding = ItemDiscoverScrollBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscoverScrollViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: DiscoverScrollViewHolder, position: Int) {}
}