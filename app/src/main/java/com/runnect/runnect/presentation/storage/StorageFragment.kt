package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentStorageBinding
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.storage.adapter.StorageAdapter
import timber.log.Timber


class StorageFragment :
    BindingFragment<FragmentStorageBinding>(R.layout.fragment_storage) {


    val viewModel: StorageViewModel by viewModels()
    private val storageAdapter = StorageAdapter(courseClickListener = {})


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.model = viewModel
        binding.lifecycleOwner = requireActivity()

        val recyclerviewStorage = binding.recyclerviewCourseList
        recyclerviewStorage.adapter = storageAdapter

        getCourse()
        toDrawCourseBtn()
        issueHandling()

    }

    private fun issueHandling() {
        viewModel.errorMessage.observe(requireActivity()) {
            if (viewModel.errorMessage.value == null) {
                Toast.makeText(requireContext(), "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                Timber.tag(ContentValues.TAG).d("fail")
                with(binding){
                    recyclerviewCourseList.isVisible = false
                    ivStorage.isVisible = true
                    tvIntroToDraw.isVisible = true
                    btnStorageDraw.isVisible = false
                }

            }
        }
        viewModel.getResult.observe(requireActivity()) {
            if (viewModel.getResult.value == null) {
                Toast.makeText(requireContext(), "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                Timber.tag(ContentValues.TAG).d(it.message)
                with(binding){
                    ivStorage.isVisible = false
                    tvIntroToDraw.isVisible = false
                    btnStorageDraw.isVisible = false
                    recyclerviewCourseList.isVisible = true
                }

                storageAdapter.submitList(it.data.courses)
            }

//            Timber.tag(ContentValues.TAG).d("it.data.courses : ${it.data.courses}")

        }
    }

    private fun toDrawCourseBtn() {
        binding.btnStorageDraw.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCourse() {
        viewModel.getCourseList()
//        Timber.tag(ContentValues.TAG).d("에러 메세지 value 확인 : ${viewModel.errorMessage.value}")
//        Timber.tag(ContentValues.TAG).d("getResult value 확인 : ${viewModel.getResult.value}")
    }


}
