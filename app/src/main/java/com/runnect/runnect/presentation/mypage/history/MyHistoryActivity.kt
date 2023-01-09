package com.runnect.runnect.presentation.mypage.history

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.model.HistoryInfoDTO
import com.runnect.runnect.databinding.ActivityMyHistoryBinding
import com.runnect.runnect.presentation.mypage.history.adapter.MyHistoryAdapter
import com.runnect.runnect.util.RecyclerOffsetDecorationHeight

class MyHistoryActivity : BindingActivity<ActivityMyHistoryBinding>(R.layout.activity_my_history) {

    private val historyList = listOf<HistoryInfoDTO>(
        HistoryInfoDTO(1,R.drawable.discover_ad_img,"test","text","test","test","test","test"),
        HistoryInfoDTO(2,R.drawable.discover_ad_img,"test","text","test","test","test","test"),
        HistoryInfoDTO(3,R.drawable.discover_ad_img,"test","text","test","test","test","test"),
        HistoryInfoDTO(4,R.drawable.discover_ad_img,"test","text","test","test","test","test")
    )
    private val adapter by lazy {
        MyHistoryAdapter(this).apply {
            submitList(historyList)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        addListener()
    }
    private fun initLayout(){
        binding.rvMyPageHistory.layoutManager = LinearLayoutManager(this)
        binding.rvMyPageHistory.addItemDecoration(RecyclerOffsetDecorationHeight(this,10))
        binding.rvMyPageHistory.adapter = adapter
    }
    private fun addListener(){
        binding.ivMyPageHistoryBack.setOnClickListener {
            finish()
        }
    }
}