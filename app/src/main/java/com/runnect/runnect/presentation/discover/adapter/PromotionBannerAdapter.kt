package com.runnect.runnect.presentation.discover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.dto.DiscoverPromotionItem
import com.runnect.runnect.databinding.ItemDiscoverPromotionBinding
import com.runnect.runnect.util.callback.ItemDiffCallback
import com.runnect.runnect.util.callback.OnBannerClick

class PromotionBannerAdapter(
    private val bannerClickListener: OnBannerClick,
) : ListAdapter<DiscoverPromotionItem,
        PromotionBannerAdapter.DiscoverPromotionViewHolder>(diffUtil) {
    private var bannerCount: Int = 0

    fun setBannerCount(bannerCount: Int) {
        this.bannerCount = bannerCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverPromotionViewHolder {
        val binding = ItemDiscoverPromotionBinding.inflate(
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
        private val binding: ItemDiscoverPromotionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: DiscoverPromotionItem) {
            binding.item = item
            binding.ivItemDiscoverPromotionBanner.setOnClickListener {
                bannerClickListener.selectBanner(item)
            }
        }
    }

    companion object {
        private const val PAGE_NUM = 900
        private val diffUtil = ItemDiffCallback<DiscoverPromotionItem>(
            onItemsTheSame = { old, new -> old.index == new.index },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}