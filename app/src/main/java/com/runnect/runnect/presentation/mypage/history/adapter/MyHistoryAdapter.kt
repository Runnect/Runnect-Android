package com.runnect.runnect.presentation.mypage.history.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.databinding.ItemMypageHistoryBinding
import com.runnect.runnect.util.HistoryInfoDiffUtilItemCallback
import com.runnect.runnect.util.callback.OnHistoryItemClick

class MyHistoryAdapter(context: Context,val listener:OnHistoryItemClick) :
    ListAdapter<HistoryInfoDTO, MyHistoryAdapter.MyHistoryViewHolder>(
        HistoryInfoDiffUtilItemCallback()
    ) {
    private val inflater by lazy { LayoutInflater.from(context) }
    private var selectedItems: MutableList<View>? = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHistoryViewHolder {
        return MyHistoryViewHolder(ItemMypageHistoryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MyHistoryViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }
    fun clearSelection(){
        selectedItems?.forEach {
            it.isSelected = false
        }
    }
    inner class MyHistoryViewHolder(private val binding: ItemMypageHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: HistoryInfoDTO) {
            with(binding) {
                Glide.with(itemView).load(data.img).thumbnail(0.3f).format(DecodeFormat.PREFER_RGB_565)
                    .into(ivMyPageHistoryCourse)
                ivMyPageHistoryCourse.load(data.img)
                tvMyPageHistoryCourseName.text = data.title
                tvMyPageHistoryPlace.text = data.location
                tvMyPageHistoryDate.text = data.date
                tvMyPageHistoryDistanceData.text = data.distance + "km"
                tvMyPageHistoryTimeData.text = data.time
                tvMyPageHistoryPaceData.text = data.pace
                ivMyPageHistoryFrame.setOnClickListener {
                    val isEditMode = listener.selectItem(it.id.toString())
                    if(isEditMode){
                        if(it.isSelected){
                            it.isSelected = false
                            selectedItems?.remove(it)
                        }
                        else{
                            it.isSelected = true
                            selectedItems?.add(it)
                        }
                    }
                }
            }
        }
    }
}

