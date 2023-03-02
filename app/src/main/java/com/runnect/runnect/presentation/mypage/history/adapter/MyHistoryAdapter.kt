package com.runnect.runnect.presentation.mypage.history.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.runnect.runnect.data.dto.RecordInfoDTO
import com.runnect.runnect.databinding.ItemMypageHistoryBinding
import com.runnect.runnect.util.HistoryInfoDiffUtilItemCallback

class MyHistoryAdapter(context: Context) :
    ListAdapter<RecordInfoDTO, MyHistoryViewHolder>(
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
    fun onBind(data: RecordInfoDTO) {
        with(binding){
            ivMyPageHistoryCourse.load(data.img)
            tvMyPageHistoryCourseName.text = data.title
            tvMyPageHistoryPlace.text = data.location
            tvMyPageHistoryDate.text = data.date
            tvMyPageHistoryDistanceData.text = data.distance + "km"
            tvMyPageHistoryTimeData.text = data.time
            tvMyPageHistoryPaceData.text = data.pace
        }
    }
}