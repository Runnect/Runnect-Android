package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentStorageScrapBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.storage.adapter.StorageScrapAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.OnScrapCourse
import timber.log.Timber


class StorageScrapFragment :
    BindingFragment<FragmentStorageScrapBinding>(R.layout.fragment_storage_scrap), OnScrapCourse,
    ItemCount {


    val viewModel: StorageViewModel by viewModels()
    private val storageScrapAdapter = StorageScrapAdapter(
        scrapClickListener = {
            Timber.tag(ContentValues.TAG).d("코스 아이디 : ${it.publicCourseId}")
            startActivity(
                Intent(activity, CourseDetailActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
                    putExtra("courseId", it.publicCourseId)
                },
            )
        },
        this, this)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = requireActivity()
        initLayout()

        val recyclerviewStorage = binding.recyclerViewStorageScrap
        recyclerviewStorage.adapter = storageScrapAdapter


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

        viewModel.itemSize.observe(viewLifecycleOwner) {
            if (viewModel.itemSize.value == 0) {
                with(binding) {
                    ivStorageNoScrap.isVisible = true
                    tvStorageNoScrapGuide.isVisible = true
                    btnStorageNoScrap.isVisible = true
                    recyclerViewStorageScrap.isVisible = false
                }
            } else {
                with(binding) {
                    ivStorageNoScrap.isVisible = false
                    tvStorageNoScrapGuide.isVisible = false
                    btnStorageNoScrap.isVisible = false
                    recyclerViewStorageScrap.isVisible = true
                }
            }
        }

        viewModel.storageState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    storageScrapAdapter.submitList(viewModel.getScrapListResult.value!!.data.scraps)
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
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
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
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


}
