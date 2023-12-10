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
    private val banners: List<DiscoverBanner>,
    private val onBannerItemClick: (String) -> Unit,
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemDiscoverBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return BannerViewHolder(binding, onBannerItemClick)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val actualPosition = position % banners.size
        holder.onBind(banners[actualPosition])
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    class BannerViewHolder(
        private val binding: ItemDiscoverBannerBinding,
        private val onBannerItemClick: (String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(banner: DiscoverBanner) {
            binding.banner = banner
            binding.ivDiscoverBanner.setOnClickListener {
                onBannerItemClick(banner.linkUrl)
            }
        }
    }
}