package com.runnect.runnect.presentation.mypage.reward

import android.content.ContentValues
import android.os.Bundle
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.api.ApiClient
import com.runnect.runnect.databinding.FragmentMyRewardBinding
import com.runnect.runnect.presentation.mypage.reward.adapter.MyRewardAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MyRewardFragment : BindingActivity<FragmentMyRewardBinding>(R.layout.fragment_my_reward) {

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
