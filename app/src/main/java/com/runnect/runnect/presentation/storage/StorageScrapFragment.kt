package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentStorageScrapBinding
import com.runnect.runnect.presentation.storage.adapter.StorageScrapAdapter
import timber.log.Timber


class StorageScrapFragment :
    BindingFragment<FragmentStorageScrapBinding>(R.layout.fragment_storage_scrap) {


    val viewModel: StorageViewModel by viewModels()
    private val storageScrapAdapter = StorageScrapAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.model = viewModel
        binding.lifecycleOwner = requireActivity()

        val recyclerviewStorage = binding.recyclerViewStorageScrap
        recyclerviewStorage.adapter = storageScrapAdapter

        getCourse()
//        toDrawCourseBtn()
        issueHandling()

    }

    private fun issueHandling() {
        viewModel.errorMessage.observe(requireActivity()) {
            if (viewModel.errorMessage.value == null) {
                Toast.makeText(requireContext(), "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                Timber.tag(ContentValues.TAG).d("fail")
//                with(binding) {
//                    recyclerviewCourseList.isVisible = false
//                    ivStorage.isVisible = true
//                    tvIntroToDraw.isVisible = true
//                    btnStorageDraw.isVisible = false
//                }

            }
        }
        viewModel.getScrapResult.observe(requireActivity()) {
            if (viewModel.getScrapResult.value == null) {
                Toast.makeText(requireContext(), "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                Timber.tag(ContentValues.TAG).d(it.message)
//                with(binding) {
//                    ivStorage.isVisible = false
//                    tvIntroToDraw.isVisible = false
//                    btnStorageDraw.isVisible = false
//                    recyclerviewCourseList.isVisible = true
//                }

                storageScrapAdapter.submitList(it.data.scraps)
            }

        }
    }


    private fun getCourse() {
        viewModel.getScrapList()
    }


}
