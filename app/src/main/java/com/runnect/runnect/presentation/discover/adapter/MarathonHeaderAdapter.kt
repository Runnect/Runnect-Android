package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverMarathonHeaderBinding

class MarathonHeaderAdapter: RecyclerView.Adapter<MarathonHeaderAdapter.MarathonHeaderViewHolder>() {
    class MarathonHeaderViewHolder(binding: ItemDiscoverMarathonHeaderBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarathonHeaderViewHolder {
        return MarathonHeaderViewHolder(
            ItemDiscoverMarathonHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MarathonHeaderViewHolder, position: Int) {}
}