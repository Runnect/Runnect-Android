package com.example.runnect.presentation.mypage

import android.content.ContentValues
import android.os.Bundle
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.data.api.ApiClient
import com.example.runnect.databinding.ActivityMyRewardBinding
import com.example.runnect.presentation.mypage.adapter.MyRewardAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MyRewardActivity : BindingActivity<ActivityMyRewardBinding>(R.layout.activity_my_reward) {

    private val getBookService = ApiClient.ServicePool.getRewardService
    private val myRewardAdapter = MyRewardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerviewReward = binding.recyclerviewReward
        recyclerviewReward.adapter = myRewardAdapter

        getRewardFromAPI()
    }

    private fun getRewardFromAPI() {

        getBookService.also {
            CoroutineScope(Dispatchers.Main).launch {
                runCatching { it.getRewardList() }
                    .onSuccess {
                        Timber.tag(ContentValues.TAG).d("success")
                        myRewardAdapter.submitList(it.body()?.items)
                    }
                    .onFailure {
                        Timber.tag(ContentValues.TAG).d("fail")
                    }
            }
        }

    }
}

