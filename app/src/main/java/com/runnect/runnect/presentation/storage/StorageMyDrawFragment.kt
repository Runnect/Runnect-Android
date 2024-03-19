package com.runnect.runnect.presentation.storage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.databinding.FragmentStorageMyDrawBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.storage.mydrawdetail.MyDrawDetailActivity
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.presentation.storage.adapter.StorageMyDrawAdapter
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName.EVENT_MY_STORAGE_TRY_REMOVE
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.listener.OnMyDrawItemClick
import com.runnect.runnect.util.custom.deco.GridSpacingItemDecoration
import com.runnect.runnect.util.extension.applyScreenEnterAnimation
import com.runnect.runnect.util.extension.setFragmentDialog
import com.runnect.runnect.util.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.view.*
import kotlinx.android.synthetic.main.fragment_storage_my_draw.*
import timber.log.Timber

@AndroidEntryPoint
class StorageMyDrawFragment :
    BindingFragment<FragmentStorageMyDrawBinding>(R.layout.fragment_storage_my_draw),
    OnMyDrawItemClick, ItemCount {
    val viewModel: StorageViewModel by viewModels()
    private lateinit var storageMyDrawAdapter: StorageMyDrawAdapter
    private lateinit var btnDeleteCourseMain: AppCompatButton
    private lateinit var bottomNavMain: BottomNavigationView
    private lateinit var animDown: Animation
    private lateinit var animUp: Animation
    private var availableEdit = false
    private var isSelectAvailable = false

    // 이 변수가 최초 할당 이후 계속 MainActivity 참조 하니까
    // 사용 안하는 순간에는 null 할당하여 참조 해제시켜줘야 함.
    private val mainActivity: MainActivity by lazy {
        activity as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        initLayout()
        initAdapter()
        getCourse()
        addListener()
        addObserver()
        pullToRefresh()
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.clickedCourseId != -1) {
            viewModel.getMyDrawDetail()
        }
    }

    private fun getCourse() {
        viewModel.getMyDrawList()
    }

    private fun pullToRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            getCourse()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun initLayout() {
        binding.recyclerViewStorageMyDraw
            .layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewStorageMyDraw.addItemDecoration(
            GridSpacingItemDecoration(
                context = requireContext(),
                spanCount = 2,
                horizontalSpaceSize = 6,
                topSpaceSize = 20
            )
        )
    }

    private fun initAdapter() {
        storageMyDrawAdapter = StorageMyDrawAdapter(this, this).apply {
            submitList(viewModel.myDrawCourses)
        } //지금 밑에 updateAdapterData()가 있는데 함수들 간 호출 시점만 잘 정해주면 둘 중 하나 없애도 될듯?
        binding.recyclerViewStorageMyDraw.adapter = storageMyDrawAdapter
    }

    private fun addListener() {
        initCourseDrawButtonClickListener()
        initCourseEditButtonClickListener()
    }

    private fun addObserver() {
        observeItemSize()
        observeStorageState()
        manageSaveDeleteBtnCondition()
        observeCourseDeleteState()
        observeCourseDetailGetState()
    }

    private fun initCourseDrawButtonClickListener() {
        binding.btnStorageNoCourse.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra(EXTRA_ROOT_SCREEN, "storageNoScrap")
            startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initCourseEditButtonClickListener() {
        binding.btnEditCourse.setOnClickListener {
            if (!availableEdit) {
                enterEditMode()
            } else {
                exitEditMode()
            }
        }
    }

    private fun enterEditMode() {
        availableEdit = true
        if (::storageMyDrawAdapter.isInitialized) storageMyDrawAdapter.handleCheckBoxVisibility(
            true
        )
        hideBottomNav()
        initCourseDeleteButtonClickListener()
        binding.tvTotalCourseCount.text = "코스 선택"
        binding.btnEditCourse.text = "취소"
        isSelectAvailable = true
    }

    @SuppressLint("SetTextI18n")
    private fun exitEditMode() {
        availableEdit = false
        isSelectAvailable = false
        if (::storageMyDrawAdapter.isInitialized) {

            storageMyDrawAdapter.clearSelection()
            storageMyDrawAdapter.handleCheckBoxVisibility(
                false
            )
        }
        binding.btnEditCourse.text = EDIT_MODE
        binding.tvTotalCourseCount.text = "총 코스 ${viewModel.myDrawCourses.size}개"

        viewModel.clearItemsToDelete()
        hideDeleteCourseBtn()
        showBottomNav()
    }

    private fun showBottomNav() {
        bottomNavMain = mainActivity.getBottomNavMain() as BottomNavigationView
        bottomNavMain.isVisible = true
    }

    private fun hideBottomNav() {
        animDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_out_down)
        bottomNavMain = mainActivity.getBottomNavMain() as BottomNavigationView
        bottomNavMain.startAnimation(animDown)
        bottomNavMain.isVisible = false
    }

    private fun initCourseDeleteButtonClickListener() {
        animUp = AnimationUtils.loadAnimation(context, R.anim.slide_out_up)
        btnDeleteCourseMain = mainActivity.getBtnDeleteCourseMain() as AppCompatButton
        btnDeleteCourseMain.isVisible = true
        btnDeleteCourseMain.isEnabled = true

        btnDeleteCourseMain.setOnClickListener {
            showCourseDeleteConfirmDialog()
        }
    }

    private fun showCourseDeleteConfirmDialog() {
        val (dialog, dialogLayout) = setFragmentDialog(
            layoutInflater = layoutInflater,
            resId = R.layout.custom_dialog_delete,
            cancel = true
        )
        with(dialogLayout) {
            this.btn_delete_yes.setOnClickListener {
                deleteCourse()
                dialog.dismiss()
                availableEdit = false
                isSelectAvailable = false
                hideDeleteCourseBtn()
                showBottomNav()
            }
            this.btn_delete_no.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun deleteCourse() {
        viewModel.deleteMyDrawCourse()
        binding.btnEditCourse.text = EDIT_MODE
    }

    private fun hideDeleteCourseBtn() {
        animDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_out_down)
        btnDeleteCourseMain = mainActivity.getBtnDeleteCourseMain() as AppCompatButton
        btnDeleteCourseMain.isVisible = false
        btnDeleteCourseMain.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    private fun observeItemSize() {
        viewModel.myDrawSize.observe(viewLifecycleOwner) { size ->
            val isEmpty = (size == 0)
            updateViewAndTotalCourseCount(isEmpty, size)
        }
    }

    private fun observeStorageState() {
        viewModel.myDrawCourseGetState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> hideLoadingBar()
                UiState.Loading -> showLoadingBar()
                UiState.Success -> {
                    hideLoadingBar()
                    showMyDrawResult()
                    updateAdapterData()
                }

                UiState.Failure -> {
                    hideLoadingBar()
                    Timber.tag(ContentValues.TAG)
                        .d("Success : getSearchList body is not null")
                }
            }
        }
    }

    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    private fun updateAdapterData() {
        storageMyDrawAdapter.submitList(viewModel.myDrawCourses)
    }

    @SuppressLint("SetTextI18n")
    private fun showMyDrawResult() {
        val isEmpty = viewModel.myDrawCourses.isEmpty()
        updateViewAndTotalCourseCount(isEmpty, viewModel.myDrawCourses.size)
    }

    @SuppressLint("SetTextI18n")
    private fun updateViewAndTotalCourseCount(isEmpty: Boolean, size: Int) {
        binding.apply {
            layoutMyDrawNoCourse.isVisible = isEmpty
            recyclerViewStorageMyDraw.isVisible = !isEmpty
            btnEditCourse.isEnabled = !isEmpty
            btnEditCourse.isVisible = !isEmpty
            tvTotalCourseCount.isVisible = !isEmpty
            tvTotalCourseCount.text = if (isEmpty) "총 코스 0개" else "총 코스 ${size}개"
        }
    }

    private fun manageSaveDeleteBtnCondition() {
        viewModel.itemsToDeleteLiveData.observe(viewLifecycleOwner) { selectedItems ->
            val count = selectedItems.size
            val deleteBtnText = if (count > 0) "삭제하기($count)" else "삭제하기"
            val deleteBtnColor = if (count > 0) R.color.M1 else R.color.G3
            updateDeleteButton(deleteBtnText, deleteBtnColor, count > 0)
        }
    }

    private fun updateDeleteButton(text: String, colorResId: Int, isEnabled: Boolean) {
        btnDeleteCourseMain = mainActivity.getBtnDeleteCourseMain() as AppCompatButton
        btnDeleteCourseMain.text = text
        btnDeleteCourseMain.setBackgroundColor(ContextCompat.getColor(requireContext(), colorResId))
        btnDeleteCourseMain.isEnabled = isEnabled
    }

    private fun observeCourseDeleteState() {
        viewModel.myDrawCourseDeleteState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> handleSuccessfulUploadDeletion()
                UiState.Failure -> handleUnsuccessfulUploadCall()
                else -> binding.indeterminateBar.isVisible = false
            }
        }
    }

    private fun handleSuccessfulUploadDeletion() {
        Analytics.logClickedItemEvent(EVENT_MY_STORAGE_TRY_REMOVE)
        binding.indeterminateBar.isVisible = false
        storageMyDrawAdapter.removeItems(viewModel.itemsToDelete)
        storageMyDrawAdapter.clearSelection()
        viewModel.clearItemsToDelete()
    }

    private fun handleUnsuccessfulUploadCall() {
        binding.indeterminateBar.isVisible = false
        Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
    }

    private fun observeCourseDetailGetState() {
        viewModel.courseDetailGetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateV2.Success -> {
                    val response = state.data
                    if (::storageMyDrawAdapter.isInitialized) {
                        storageMyDrawAdapter.updateCourseTitle(response.courseId, response.title)
                    }
                }

                is UiStateV2.Failure -> {
                    context?.showSnackbar(anchorView = binding.root, message = state.msg)
                }

                else -> {}
            }
        }
    }

    override fun selectItem(id: Int, title: String): Boolean {
        return if (!isSelectAvailable) {
            viewModel.saveClickedCourseId(id)
            Intent(context, MyDrawDetailActivity::class.java).apply {
                putExtra(EXTRA_COURSE_ID, id)
                putExtra(EXTRA_COURSE_TITLE, title)
                startActivity(this)
            }
            activity?.applyScreenEnterAnimation()
            false
        } else {
            viewModel.modifyItemsToDelete(id)
            true
        }
    }

    override fun calcItemSize(itemCount: Int) {
        viewModel.myDrawSize.value = itemCount
    }

    companion object {
        const val EXTRA_COURSE_ID = "courseId"
        const val EXTRA_COURSE_TITLE = "courseTitle"
        const val EXTRA_ROOT_SCREEN = "rootScreen"
        const val EDIT_MODE = "선택"
    }
}
