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
                var itemCount = currentList.size
                Timber.d("삭제 전 itemCount 값? ${itemCount}")
                ivItemStorageScrapHeart.isSelected = true
                ivItemStorageScrapHeart.setOnClickListener {
                    ivItemStorageScrapHeart.isSelected = false
                    deleteItem(adapterPosition)
                    Timber.d("삭제 후 itemCount 값? ${itemCount}") //여기가 2가 나와야 되는데 왜 3이지지                    Timber.d("통신에 넣어줄 값? ${data.publicCourseId}, ${it.isSelected}")
                    mCallback.scrapCourse(data.publicCourseId, it.isSelected)
                    //이렇게하면 삭제는 되는데 emptyList가 됐을 때 '스크랩하러 가기'가 안떠.
                    //이유는, 여기서는 터치가 일어나서 item을 삭제할 때마다 scrap 통신이 일어나게끔 설계가 돼있어.
                    //그런데 Fragment에서는 탭을 통해서 이 fragment로 처음 들어왔을 때만 서버 통신이 일어나게 돼있어서 터치랑 연결이 안 되는 거야.
                    //이걸 해결하려면 서버통신이 처음 fragment 들어왔을 때 말고도 일어나게 해주거나 visible 조건 자체를 다른 데다 넣는 방법이 있어.
                }

                root.setOnClickListener {
                    listener(data)
                }
            }
        }


        fun bind(scrapList: ResponseGetScrapDto.Data.Scrap) {
            binding.storageScrap = scrapList
        }


    }

    fun deleteItem(position: Int){
        val itemList = mutableListOf<ResponseGetScrapDto.Data.Scrap>()
        itemList.addAll(currentList)
        itemList.removeAt(position)
        submitList(itemList)
        Timber.d("itemList? ${itemList}")
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
