package com.runnect.runnect.presentation.storage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.data.dto.MyScrapCourse
import com.runnect.runnect.databinding.FragmentStorageScrapBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.storage.adapter.StorageScrapAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.OnHeartClick
import com.runnect.runnect.util.callback.OnScrapCourseClick
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StorageScrapFragment :
    BindingFragment<FragmentStorageScrapBinding>(R.layout.fragment_storage_scrap), OnHeartClick,
    OnScrapCourseClick,
    ItemCount {


    val viewModel: StorageViewModel by viewModels()
    lateinit var storageScrapAdapter: StorageScrapAdapter

    companion object {
        var isFromStorageNoScrap = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = requireActivity()
        initLayout()
        initAdapter()
        getCourse()
        toScrapCourseBtn()
        addObserver()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            MainActivity.storageScrapFragment = this
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.storageScrapFragment = null
    }

    override fun scrapCourse(id: Int?, scrapTF: Boolean) {
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
            layoutMyDrawNoScrap.isVisible = true
            recyclerViewStorageScrap.isVisible = false
            tvTotalScrapCount.isVisible = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun hideEmptyView() {
        with(binding) {
            layoutMyDrawNoScrap.isVisible = false
            recyclerViewStorageScrap.isVisible = true
            tvTotalScrapCount.isVisible = true
            tvTotalScrapCount.text =
                "총 코스 ${viewModel.getScrapListResult.value!!.size}개" // 같은 기능에 대해 코드가 나눠져 있어서 하나로 처리할 수 있는 방법에 대해 고민중
        }
    }


    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    @SuppressLint("SetTextI18n")
    private fun observeItemSize() {
        viewModel.itemSize.observe(viewLifecycleOwner) {
            if (viewModel.itemSize.value == 0) {
                showEmptyView()
            } else {
                hideEmptyView()
            }
            binding.tvTotalScrapCount.text =
                "총 코스 ${viewModel.itemSize.value}개" // 같은 기능에 대해 코드가 나눠져 있어서 하나로 처리할 수 있는 방법에 대해 고민중
        }
    }

    private fun showScarpResult() {
        if (viewModel.getScrapListResult.value!!.isEmpty()) {
            showEmptyView()
        } else {
            hideEmptyView()
        }
    }

    private fun updateAdapterData() {
        storageScrapAdapter.submitList(viewModel.getScrapListResult.value!!)
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
            isFromStorageNoScrap = true

            val intent = Intent(activity, MainActivity::class.java).apply {
                putExtra("fromScrapFragment", true)
            }
            startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }


    fun getCourse() {
        viewModel.getScrapList()
    }

    override fun calcItemSize(itemCount: Int) {
        viewModel.itemSize.value = itemCount
    }

    private fun initAdapter() {
        storageScrapAdapter = StorageScrapAdapter(this, this, this)
        binding.recyclerViewStorageScrap.adapter = storageScrapAdapter
    }

    override fun selectItem(item: MyScrapCourse) {
        Timber.tag(ContentValues.TAG).d("코스 아이디 : ${item.publicCourseId}")

        val intent = Intent(activity, CourseDetailActivity::class.java)
        intent.putExtra("publicCourseId", item.publicCourseId)
        intent.putExtra("root", "storageScrap")
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }
}
