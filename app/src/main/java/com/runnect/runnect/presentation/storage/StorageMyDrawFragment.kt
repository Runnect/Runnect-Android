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
import com.runnect.runnect.databinding.FragmentStorageMyDrawBinding
import com.runnect.runnect.presentation.mydrawdetail.MyDrawDetailActivity
import com.runnect.runnect.presentation.search.SearchActivity
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
        toDrawCourseBtn()
        issueHandling()

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

    private fun issueHandling() {
        viewModel.errorMessage.observe(requireActivity()) {
            if (viewModel.errorMessage.value == null) {
                Toast.makeText(requireContext(), "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                Timber.tag(ContentValues.TAG).d("fail")
                with(binding) {
                    recyclerViewStorageMyDraw.isVisible = false
                    ivStorageMyDrawNoCourse.isVisible = true
                    tvStorageMyDrawNoCourseGuide.isVisible = true
                    btnStorageNoCourse.isVisible = true
                }

            }
        }
        viewModel.getMyDrawResult.observe(requireActivity()) {
            if (viewModel.getMyDrawResult.value == null) {
                Toast.makeText(requireContext(), "서버에 문제가 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                Timber.tag(ContentValues.TAG).d(it.message)
                with(binding) {
                    recyclerViewStorageMyDraw.isVisible = true
                    ivStorageMyDrawNoCourse.isVisible = false
                    tvStorageMyDrawNoCourseGuide.isVisible = false
                    btnStorageNoCourse.isVisible = false
                }

                storageMyDrawAdapter.submitList(it.data.courses)
            }

        }
    }

    private fun toDrawCourseBtn() {
        binding.btnStorageNoCourse.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCourse() {
        viewModel.getMyDrawList()
    }


}
