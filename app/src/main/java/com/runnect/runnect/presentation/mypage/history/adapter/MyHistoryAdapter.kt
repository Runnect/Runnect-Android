package com.runnect.runnect.presentation.mypage.history.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.model.HistoryInfoDTO
import com.runnect.runnect.databinding.ItemMypageHistoryBinding
import com.runnect.runnect.util.HistoryInfoDiffUtilItemCallback

class MyHistoryAdapter(context: Context) :
    ListAdapter<HistoryInfoDTO, MyHistoryViewHolder>(
        HistoryInfoDiffUtilItemCallback()
    ) {
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHistoryViewHolder {
        return MyHistoryViewHolder(ItemMypageHistoryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MyHistoryViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }
}

class MyHistoryViewHolder(private val binding: ItemMypageHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: HistoryInfoDTO) {
        binding.ivMyPageHistoryCourse.setImageResource(data.img)
        binding.tvMyPageHistoryDate.text = data.title
        binding.tvMyPageHistoryPlace.text = data.location
    }
}