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
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.storage.adapter.StorageScrapAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration
import timber.log.Timber


class StorageScrapFragment :
    BindingFragment<FragmentStorageScrapBinding>(R.layout.fragment_storage_scrap) {


    val viewModel: StorageViewModel by viewModels()
    private val storageScrapAdapter = StorageScrapAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = requireActivity()
        initLayout()

        val recyclerviewStorage = binding.recyclerViewStorageScrap
        recyclerviewStorage.adapter = storageScrapAdapter


        getCourse()
        toScrapCourseBtn()
        issueHandling()

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

    private fun issueHandling() {
        viewModel.errorMessage.observe(requireActivity()) {
            if (viewModel.errorMessage.value == null) {
                Toast.makeText(requireContext(), "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                Timber.tag(ContentValues.TAG).d(it)

            }
        }
        viewModel.getScrapResult.observe(requireActivity()) {
            if (viewModel.getScrapResult.value == null) {
                Toast.makeText(requireContext(), "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                Timber.tag(ContentValues.TAG).d(it.message)

                if (it.data.scraps.isEmpty()) {
                    with(binding) { //이게 여기가 아니라 밑에 있어야 되는거였네
                        recyclerViewStorageScrap.isVisible = false
                        ivStorageNoScrap.isVisible = true
                        tvStorageNoScrapGuide.isVisible = true
                        btnStorageNoScrap.isVisible = true
                    }
                } else {
                    with(binding) {
                        recyclerViewStorageScrap.isVisible = true
                        ivStorageNoScrap.isVisible = false
                        tvStorageNoScrapGuide.isVisible = false
                        btnStorageNoScrap.isVisible = false
                    }
                }


                storageScrapAdapter.submitList(it.data.scraps)
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


}
