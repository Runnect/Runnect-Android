package com.runnect.runnect.presentation.discover

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentDiscoverBinding
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.discover.adapter.CourseRecommendAdapter
import com.runnect.runnect.presentation.discover.load.DiscoverLoadActivity
import com.runnect.runnect.presentation.discover.search.DiscoverSearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.OnItemClick
import com.runnect.runnect.util.callback.OnScrapCourse
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover),
    OnItemClick, OnScrapCourse {
    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var adapter: CourseRecommendAdapter
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        initLayout()
        viewModel.getRecommendCourse()
        addListener()
        addObserver()
        setResultDetail()
    }

    private fun initLayout() {
        binding.rvDiscoverRecommend.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDiscoverRecommend.addItemDecoration(
            GridSpacingItemDecoration(
                requireContext(),
                2,
                6,
                16
            )
        )
    }

    private fun addListener() {
        binding.ivDiscoverSearch.setOnClickListener {
            startActivity(Intent(requireContext(), DiscoverSearchActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
        binding.btnDiscoverUpload.setOnClickListener {
            startActivity(Intent(requireContext(), DiscoverLoadActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }

    private fun addObserver() {
        viewModel.courseInfoState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false
                    initAdapter()
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Failure : ${viewModel.errorMessage.value}")
                }
            }
        }
    }


    private fun initAdapter() {
        adapter = CourseRecommendAdapter(requireContext(), this, this).apply {
            submitList(
                viewModel.recommendCourseList
            )
        }

        binding.rvDiscoverRecommend.setHasFixedSize(true)
        binding.rvDiscoverRecommend.adapter = adapter
    }


    override fun scrapCourse(id: Int, scrapTF: Boolean) {
        viewModel.postCourseScrap(id, scrapTF)
    }

    private fun setResultDetail() {
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.getRecommendCourse()
            }
        }
    }

    override fun selectItem(id: Int) {
        val intent = Intent(requireContext(), CourseDetailActivity::class.java)
        intent.putExtra("courseId", id)
        startForResult.launch(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }
}
