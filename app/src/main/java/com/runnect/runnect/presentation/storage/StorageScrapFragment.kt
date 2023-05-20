package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.data.model.ResponseGetScrapDto
import com.runnect.runnect.databinding.FragmentStorageScrapBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.storage.adapter.StorageScrapAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnScrapCourseClick
import timber.log.Timber


class StorageScrapFragment :
    BindingFragment<FragmentStorageScrapBinding>(R.layout.fragment_storage_scrap), OnHeartClick,
    OnScrapCourseClick,
    ItemCount {


    val viewModel: StorageViewModel by viewModels()
    lateinit var storageScrapAdapter: StorageScrapAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = requireActivity()
        initLayout()
        initAdapter()
        getCourse()
        toScrapCourseBtn()
        addObserver()

    }

    override fun scrapCourse(id: Int, scrapTF: Boolean) {
        viewModel.postCourseScrap(id, scrapTF)
    }

    private fun initLayout() {
        binding.recyclerViewStorageScrap
            .layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewStorageScrap.addItemDecoration(
            GridSpacingItemDecoration(
                requireContext(),
                2,
                6,
                16
            )
        )
    }

    private fun addObserver() {
        observeItemSize()
        observeStorageState()
    }

    private fun showEmptyView() {
        with(binding) {
            ivStorageNoScrap.isVisible = true
            tvStorageNoScrapGuide.isVisible = true
            btnStorageNoScrap.isVisible = true
            recyclerViewStorageScrap.isVisible = false
            tvTotalScrapCount.text = "총 코스 0개"
        }
    }

    private fun hideEmptyView() {
        with(binding) {
            ivStorageNoScrap.isVisible = false
            tvStorageNoScrapGuide.isVisible = false
            btnStorageNoScrap.isVisible = false
            recyclerViewStorageScrap.isVisible = true
            tvTotalScrapCount.text = "총 코스 ${viewModel.getScrapListResult.value!!.data.scraps.size}개"
        }
    }


    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    private fun observeItemSize() {
        viewModel.itemSize.observe(viewLifecycleOwner) {
            if (viewModel.itemSize.value == 0) {
                showEmptyView()
            } else {
                hideEmptyView()
            }
            binding.tvTotalScrapCount.text = "총 코스 ${viewModel.itemSize.value}개"
        }
    }

    private fun showScarpResult() {
        if (viewModel.getScrapListResult.value!!.data.scraps.isEmpty()) {
            showEmptyView()
        } else {
            hideEmptyView()
        }
    }

    private fun updateAdapterData() {
        storageScrapAdapter.submitList(viewModel.getScrapListResult.value!!.data.scraps)
    }

    private fun observeStorageState() {
        viewModel.storageState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> hideLoadingBar()
                UiState.Loading -> showLoadingBar()
                UiState.Success -> {
                    hideLoadingBar()
                    showScarpResult()
                    updateAdapterData()
                }
                UiState.Failure -> {
                    hideLoadingBar()
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage}")
                }
            }
        }
    }

    private fun toScrapCourseBtn() {
        binding.btnStorageNoScrap.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java).apply {
                putExtra("fromScrapFragment", true)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            } //임의의 intent
            startActivity(intent)
        }
    }


    private fun getCourse() {
        viewModel.getScrapList()
    }

    override fun calcItemSize(itemCount: Int) {
        viewModel.itemSize.value = itemCount
    }

    private fun initAdapter() {
        storageScrapAdapter = StorageScrapAdapter(this, this, this)
        binding.recyclerViewStorageScrap.adapter = storageScrapAdapter
    }

    override fun selectItem(item: ResponseGetScrapDto.Data.Scrap) {
        Timber.tag(ContentValues.TAG).d("코스 아이디 : ${item.publicCourseId}")
        startActivity(
            Intent(activity, CourseDetailActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra("courseId", item.publicCourseId)
            },
        )

    }


}
