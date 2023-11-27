package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.databinding.ItemDiscoverBannerBinding
import com.runnect.runnect.domain.entity.PromotionBanner
import com.runnect.runnect.util.callback.diff.ItemDiffCallback
import com.runnect.runnect.util.callback.listener.OnBannerItemClick

class BannerAdapter(
    private val onBannerItemClick: OnBannerItemClick,
) : ListAdapter<PromotionBanner,
        BannerAdapter.DiscoverPromotionViewHolder>(diffUtil) {
    private var bannerCount: Int = 0

    fun setBannerCount(bannerCount: Int) {
        this.bannerCount = bannerCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverPromotionViewHolder {
        val binding = ItemDiscoverBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return DiscoverPromotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscoverPromotionViewHolder, position: Int) {
        holder.onBind(getItem(position % bannerCount))
    }

    override fun getItemCount(): Int = PAGE_NUM

    inner class DiscoverPromotionViewHolder(
        private val binding: ItemDiscoverBannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(banner: PromotionBanner) {
            binding.banner = banner
            binding.ivItemDiscoverPromotionBanner.setOnClickListener {
                onBannerItemClick.selectBanner(banner)
            }
        }
    }

    companion object {
        private const val PAGE_NUM = 900
        private val diffUtil = ItemDiffCallback<PromotionBanner>(
            onItemsTheSame = { old, new -> old.index == new.index },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}