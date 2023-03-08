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
import com.runnect.runnect.databinding.FragmentStorageMyDrawBinding
import com.runnect.runnect.presentation.mydrawdetail.MyDrawDetailActivity
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.storage.adapter.StorageMyDrawAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration
import timber.log.Timber


class StorageMyDrawFragment :
    BindingFragment<FragmentStorageMyDrawBinding>(R.layout.fragment_storage_my_draw) {


    val viewModel: StorageViewModel by viewModels()
    private val storageMyDrawAdapter = StorageMyDrawAdapter(courseClickListener = {
        Timber.tag(ContentValues.TAG).d("코스 아이디 : ${it.id}")
        startActivity(Intent(activity, MyDrawDetailActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
            putExtra("fromStorageFragment", it.id)
        })
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        binding.lifecycleOwner = requireActivity()

        val recyclerviewStorage = binding.recyclerViewStorageMyDraw
        recyclerviewStorage.adapter = storageMyDrawAdapter

        getCourse()
        addData()
        addObserver()

    }

    private fun initLayout() {
        binding.recyclerViewStorageMyDraw
            .layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewStorageMyDraw.addItemDecoration(
            GridSpacingItemDecoration(
                requireContext(),
                2,
                6,
                16
            )
        )
    }


    private fun addObserver() {

        viewModel.storageState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false

                    if (viewModel.getMyDrawResult.value!!.data.courses.isEmpty()) {
                        with(binding) {
                            ivStorageMyDrawNoCourse.isVisible = true
                            tvStorageMyDrawNoCourseGuide.isVisible = true
                            btnStorageNoCourse.isVisible = true
                            recyclerViewStorageMyDraw.isVisible = false
                        }
                    } else {
                        with(binding) {
                            ivStorageMyDrawNoCourse.isVisible = false
                            tvStorageMyDrawNoCourseGuide.isVisible = false
                            btnStorageNoCourse.isVisible = false
                            recyclerViewStorageMyDraw.isVisible = true
                        }
                    }
                    storageMyDrawAdapter.submitList(viewModel.getMyDrawResult.value!!.data.courses)
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Success : getSearchList body is not null")
                }

            }

        }

    }

    private fun addData() {
        binding.btnStorageNoCourse.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
            }
            startActivity(intent)
        }
    }

    private fun getCourse() {
        viewModel.getMyDrawList()
    }
}




