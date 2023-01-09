package com.runnect.runnect.presentation.discover.load

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.model.CourseLoadInfoDTO
import com.runnect.runnect.databinding.ActivityDiscoverLoadSelectBinding
import com.runnect.runnect.presentation.discover.load.adapter.DiscoverLoadAdapter
import com.runnect.runnect.presentation.discover.upload.DiscoverUploadActivity
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.OnItemClick
import timber.log.Timber

class DiscoverLoadActivity :
    BindingActivity<ActivityDiscoverLoadSelectBinding>(R.layout.activity_discover_load_select),
    OnItemClick {
    private val viewModel: DiscoverLoadViewModel by viewModels()
    private val courseLoadInfoList by lazy {
        listOf(
            CourseLoadInfoDTO(
                1,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "서울시 송파구 한강",
            ),
            CourseLoadInfoDTO(
                2,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "서울시 송파구 한강"
            ),
            CourseLoadInfoDTO(
                3,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "서울시 송파구 한강"
            ),
            CourseLoadInfoDTO(
                4,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "서울시 송파구 한강"
            ),
            CourseLoadInfoDTO(
                5,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "서울시 송파구 한강"
            ),
            CourseLoadInfoDTO(
                6,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "서울시 송파구 한강"
            ),
            CourseLoadInfoDTO(
                7,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "서울시 송파구 한강"
            ),
            CourseLoadInfoDTO(
                8,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "aa"
            ),
            CourseLoadInfoDTO(
                9,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "aa"
            ),
            CourseLoadInfoDTO(
                10,
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",
                "aa"
            )
        )
    }
    private val adapter by lazy {
        DiscoverLoadAdapter(this, this).apply {
            submitList(
                courseLoadInfoList
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        initLayout()
        addObserver()
        addListener()
    }

    private fun initLayout() {
        binding.rvDiscoverLoadSelect.apply {
            layoutManager = GridLayoutManager(this@DiscoverLoadActivity, 2)
            adapter = this@DiscoverLoadActivity.adapter
            addItemDecoration(
                GridSpacingItemDecoration(
                    this@DiscoverLoadActivity, 2,
                    6,
                    42
                )
            )
        }
    }

    private fun addObserver() {
        viewModel.idSelectedItem.observe(this) {
            Timber.d("4. ViewModel에서 변경된 라이브데이터 관찰")
            binding.ivDiscoverLoadSelectFinish.isActivated = it != 0
        }
    }

    private fun addListener() {
        binding.ivDiscoverLoadSelectBack.setOnClickListener {
            finish()
        }
        binding.ivDiscoverLoadSelectFinish.setOnClickListener {
            if (it.isActivated) {
                val intent = Intent(this, DiscoverUploadActivity::class.java)
                intent.putExtra("courseId", viewModel.idSelectedItem.value)
                startActivity(intent)
            }
        }
    }

    override fun selectItem(id: Int) {
        Timber.d("2. Adapter로부터 호출되는 콜백함수 selectItem")
        viewModel.checkSelectEnable(id)
    }
}