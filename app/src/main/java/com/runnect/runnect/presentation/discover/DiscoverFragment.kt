package com.runnect.runnect.presentation.discover

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentDiscoverBinding
import com.runnect.runnect.util.ItemOffsetDecoration

class DiscoverFragment : BindingFragment<FragmentDiscoverBinding>(R.layout.fragment_discover) {
    private val viewModel: DiscoverViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initLayout()
    }

    private fun initLayout() {
        binding.rvDiscoverRecommend.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDiscoverRecommend.addItemDecoration(ItemOffsetDecoration(requireContext(), 3, 16))
    }
}