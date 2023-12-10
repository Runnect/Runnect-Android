package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverBannerBinding
import com.runnect.runnect.domain.entity.DiscoverBanner
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import timber.log.Timber

class BannerAdapter(
    private val onBannerItemClick: (String) -> Unit,
) : ListAdapter<DiscoverBanner, BannerAdapter.BannerViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemDiscoverBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class BannerViewHolder(
        private val binding: ItemDiscoverBannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(banner: DiscoverBanner) {
            binding.banner = banner
            binding.ivDiscoverBanner.setOnClickListener {
                onBannerItemClick(banner.linkUrl)
            }
        }
    }

    companion object {
        private val diffUtil = ItemDiffCallback<DiscoverBanner>(
            onItemsTheSame = { old, new -> old.index == new.index },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}