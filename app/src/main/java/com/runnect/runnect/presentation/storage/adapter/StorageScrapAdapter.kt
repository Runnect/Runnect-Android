package com.runnect.runnect.presentation.storage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.databinding.ItemStorageScrapBinding
import com.runnect.runnect.util.callback.OnScrapCourse
import timber.log.Timber

class StorageScrapAdapter(
    scrapClickListener: (ResponseGetScrapDto.Data.Scrap) -> Unit,
    heartListener: OnScrapCourse,
) :
    ListAdapter<ResponseGetScrapDto.Data.Scrap, StorageScrapAdapter.ItemViewHolder>(Differ()) {

    private var mCallback = heartListener
    private val listener = scrapClickListener

    inner class ItemViewHolder(val binding: ItemStorageScrapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: ResponseGetScrapDto.Data.Scrap) {
            with(binding) {
                ivItemStorageScrapHeart.isSelected =
                    true //상세페이지 어댑터에서는 받아온 data에 따라 true/false 세팅해주면 되는데
                // 여기선 ResponseGetScrapDto.Data.Scrap에 ScrapTF가 없으니까 내가 임의로 true라고 해놓음.
                ivItemStorageScrapHeart.setOnClickListener {
                    ivItemStorageScrapHeart.isSelected =
                        false //터치가 됐다는 건 스크랩을 취소하겠다는 거니까 false로 만들어서 하트 비워주고
                    Timber.d("통신에 넣어줄 값? ${data.publicCourseId}, ${it.isSelected}")
                    mCallback.scrapCourse(data.publicCourseId, it.isSelected) // <- 여기에 스크랩 취소 통신에 쓰일 data 넣기
                } //여기까지 하면 기본적인 구현은 된 건데 스크랩 취소할 때마다 리사이클러뷰 UI 갱신을 시켜주고 싶은데 이거까진 안 돼있음.

                root.setOnClickListener {
                    listener(data)
                }
            }
        }


        fun bind(scrapList: ResponseGetScrapDto.Data.Scrap) {
            binding.storageScrap = scrapList
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStorageScrapBinding.inflate(inflater)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.onBind(currentList[position])
    }


    class Differ : DiffUtil.ItemCallback<ResponseGetScrapDto.Data.Scrap>() {
        override fun areItemsTheSame(
            oldItem: ResponseGetScrapDto.Data.Scrap,
            newItem: ResponseGetScrapDto.Data.Scrap,
        ): Boolean {
            return oldItem.courseId == newItem.courseId
        }

        override fun areContentsTheSame(
            oldItem: ResponseGetScrapDto.Data.Scrap,
            newItem: ResponseGetScrapDto.Data.Scrap,
        ): Boolean {
            return oldItem == newItem
        }

    }
}
