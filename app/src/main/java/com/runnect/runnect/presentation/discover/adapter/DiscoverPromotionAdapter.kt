package com.runnect.runnect.presentation.discover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.runnect.runnect.data.dto.DiscoverPromotionItemDTO
import com.runnect.runnect.databinding.ItemDiscoverPromotionBinding
import com.runnect.runnect.util.callback.OnBannerClick

class DiscoverPromotionAdapter(
    val context: Context,
    private val bannerClickListener: OnBannerClick,
) :

    ListAdapter<DiscoverPromotionItemDTO, DiscoverPromotionAdapter.DiscoverPromotionViewHolder>(
        DiscoverPromotionDiff
    ) {
    private var bannerCount: Int = 0
    lateinit var binding: ItemDiscoverPromotionBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverPromotionViewHolder {
        binding =
            ItemDiscoverPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscoverPromotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscoverPromotionViewHolder, position: Int) {
        holder.onBind(getItem(position % bannerCount))
    }
    fun setBannerCount(bannerCount: Int){
        this.bannerCount = bannerCount
    }

    override fun getItemCount(): Int = PAGE_NUM

    inner class DiscoverPromotionViewHolder(private val binding: ItemDiscoverPromotionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: DiscoverPromotionItemDTO) {
            with(binding.ivItemDiscoverPromotionBanner) {
                Glide.with(context)
                    .load(item.imageUrl)
                    .thumbnail(0.3f)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(binding.ivItemDiscoverPromotionBanner)
                setOnClickListener {
                    bannerClickListener.selectBanner(item)
                }
            }
        }
    }

    companion object {
        const val PAGE_NUM = 900
    }
}

object DiscoverPromotionDiff : DiffUtil.ItemCallback<DiscoverPromotionItemDTO>() {
    override fun areItemsTheSame(
        oldItem: DiscoverPromotionItemDTO,
        newItem: DiscoverPromotionItemDTO
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: DiscoverPromotionItemDTO,
        newItem: DiscoverPromotionItemDTO
    ): Boolean {
        return oldItem == newItem
    }
}