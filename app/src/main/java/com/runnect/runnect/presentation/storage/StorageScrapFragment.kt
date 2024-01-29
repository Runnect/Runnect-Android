package com.runnect.runnect.presentation.storage

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
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.databinding.FragmentStorageScrapBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.detail.CourseDetailActivity
import com.runnect.runnect.presentation.detail.CourseDetailRootScreen
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.presentation.storage.adapter.StorageScrapAdapter
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.listener.OnHeartButtonClick
import com.runnect.runnect.util.callback.listener.OnScrapItemClick
import com.runnect.runnect.util.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StorageScrapFragment : BindingFragment<FragmentStorageScrapBinding>(R.layout.fragment_storage_scrap),
    OnHeartButtonClick,
    OnScrapItemClick,
    ItemCount {
    val viewModel: StorageViewModel by viewModels()
    private lateinit var storageScrapAdapter: StorageScrapAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        getMyScrapCourses()
        initLayout()
        initAdapter()
        addListener()
        addObserver()
    }

    fun getMyScrapCourses() {
        viewModel.getMyScrapCoures()
    }

    private fun initLayout() {
        binding.recyclerViewStorageScrap.apply {
            val context = context ?: return
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(
                GridSpacingItemDecoration(
                    context = context,
                    spanCount = 2,
                    horizontalSpaceSize = 6,
                    topSpaceSize = 20
                )
            )
        }
    }

    private fun initAdapter() {
        storageScrapAdapter = StorageScrapAdapter(
            onScrapItemClick = this,
            onHeartButtonClick = this,
            itemCount = this
        ).apply {
            binding.recyclerViewStorageScrap.adapter = this
        }
    }

    private fun addListener() {
        initGoToScrapButtonClickListener()
        initRefreshLayoutListener()
    }

    private fun initGoToScrapButtonClickListener() {
        binding.btnStorageNoScrap.setOnClickListener {
            isFromStorageScrap = true

            val intent = Intent(activity, MainActivity::class.java).apply {
                putExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION, "fromMyScrap")
            }
            startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }

    private fun initRefreshLayoutListener() {
        binding.refreshLayout.setOnRefreshListener {
            getMyScrapCourses()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun addObserver() {
        setupItemSizeObserver()
        setupMyScrapCourseGetStateObserver()
    }

    private fun setupItemSizeObserver() {
        viewModel.itemSize.observe(viewLifecycleOwner) { itemSize ->
            val isEmpty = (itemSize == 0)
            updateEmptyView(isEmpty, itemSize)
        }
    }

    private fun updateEmptyView(isEmpty: Boolean, itemSize: Int) {
        binding.apply {
            layoutMyDrawNoScrap.isVisible = isEmpty
            recyclerViewStorageScrap.isVisible = !isEmpty
            tvTotalScrapCount.isVisible = !isEmpty
            tvTotalScrapCount.text = if (!isEmpty) "총 코스 ${itemSize}개" else ""
        }
    }

    private fun setupMyScrapCourseGetStateObserver() {
        viewModel.myScrapCoursesGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Loading -> {
                    showLoadingProgressBar()
                }

                is UiStateV2.Success -> {
                    dismissLoadingProgressBar()

                    val scrapCourses = state.data
                    updateEmptyView(scrapCourses.isEmpty(), scrapCourses.size)
                    storageScrapAdapter.submitList(scrapCourses)
                }

                is UiStateV2.Failure -> {
                    dismissLoadingProgressBar()
                    context?.showSnackbar(
                        anchorView = binding.root,
                        message = state.msg
                    )
                }

                else -> {}
            }
        }
    }

    private fun showLoadingProgressBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun dismissLoadingProgressBar() {
        binding.indeterminateBar.isVisible = false
    }

    override fun calcItemSize(itemCount: Int) {
        viewModel.itemSize.value = itemCount
    }

    override fun scrapCourse(id: Int, scrapTF: Boolean) {
        viewModel.postCourseScrap(id, scrapTF)
    }

    override fun selectItem(item: MyScrapCourse) {
        Timber.tag(ContentValues.TAG).d("코스 아이디 : ${item.publicCourseId}")

        val intent = Intent(activity, CourseDetailActivity::class.java)
        intent.putExtra(EXTRA_PUBLIC_COURSE_ID, item.publicCourseId)
        intent.putExtra(EXTRA_ROOT_SCREEN, CourseDetailRootScreen.COURSE_STORAGE_SCRAP)
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
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

    companion object {
        var isFromStorageScrap = false
        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        const val EXTRA_PUBLIC_COURSE_ID = "publicCourseId"
        const val EXTRA_ROOT_SCREEN = "rootScreen"
    }
}
