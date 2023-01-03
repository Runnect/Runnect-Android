package com.runnect.runnect.presentation.mypage.reward.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.runnect.runnect.databinding.ItemMypageRewardBinding
//
//class MyRewardAdapter :
//    ListAdapter<GetRewardDto.Item, MyRewardAdapter.ItemViewHolder>(Differ()) {
//
//
//    inner class ItemViewHolder(val binding: ItemMypageRewardBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//
//        fun bind(rewardList: GetRewardDto.Item) {
//
//            binding.reward = rewardList
//
//
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemMypageRewardBinding.inflate(inflater)
//
//        return ItemViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//        holder.bind(currentList[position])
//    }
//
//
//    class Differ : DiffUtil.ItemCallback<GetRewardDto.Item>() {
//        override fun areItemsTheSame(
//            oldItem: GetRewardDto.Item,
//            newItem: GetRewardDto.Item
//        ): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(
//            oldItem: GetRewardDto.Item,
//            newItem: GetRewardDto.Item
//        ): Boolean {
//            return oldItem == newItem
//        }
//
//    }
//}
