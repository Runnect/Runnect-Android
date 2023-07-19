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
import com.runnect.runnect.presentation.mydrawdetail.MyDrawDetailActivity
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.storage.adapter.StorageMyDrawAdapter
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.ItemCount
import com.runnect.runnect.util.callback.OnMyDrawClick
import com.runnect.runnect.util.extension.setFragmentDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.view.*
import kotlinx.android.synthetic.main.fragment_storage_my_draw.*
import timber.log.Timber

@AndroidEntryPoint
class StorageMyDrawFragment :
    BindingFragment<FragmentStorageMyDrawBinding>(R.layout.fragment_storage_my_draw),
    OnMyDrawClick, ItemCount {

    val viewModel: StorageViewModel by viewModels()

    lateinit var storageMyDrawAdapter: StorageMyDrawAdapter


    private val mainActivity: MainActivity by lazy {
        activity as MainActivity
    } // 이 변수가 최초 할당 이후 계속 MainActivity를 참조하니까 사용을 안 하는 순간에는 null을 할당해줌으로써 메모리에서 내려줘야 함.

    lateinit var btnDeleteCourseMain: AppCompatButton
    lateinit var bottomNavMain: BottomNavigationView

    private lateinit var animDown: Animation
    private lateinit var animUp: Animation

    var availableEdit = false
    var isSelectAvailable = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout()
        binding.lifecycleOwner = requireActivity()
        initAdapter()
        editCourse()
        getCourse()
        requireCourse()
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

    private fun initAdapter() {
        storageMyDrawAdapter = StorageMyDrawAdapter(this, this).apply {
            submitList(viewModel.myDrawCourses)
        } //지금 밑에 updateAdapterData()가 있는데 함수들 간 호출 시점만 잘 정해주면 둘 중 하나 없애도 될듯?
        binding.recyclerViewStorageMyDraw.adapter = storageMyDrawAdapter
    }

    fun hideBottomNav() {
        animDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_out_down)

        bottomNavMain =
            mainActivity.getBottomNavMain() as BottomNavigationView

        bottomNavMain.startAnimation(animDown)
        bottomNavMain.isVisible = false
    }

    fun showBottomNav() {
        bottomNavMain =
            mainActivity.getBottomNavMain() as BottomNavigationView
        bottomNavMain.isVisible = true
    }

    private fun hideDeleteCourseBtn() {
        animDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_out_down)

        btnDeleteCourseMain =
            mainActivity.getBtnDeleteCourseMain() as AppCompatButton

        btnDeleteCourseMain.isVisible = false
        btnDeleteCourseMain.isEnabled = false
    }

    private fun showDeleteCourseBtn() {
        animUp = AnimationUtils.loadAnimation(context, R.anim.slide_out_up)

        btnDeleteCourseMain =
            mainActivity.getBtnDeleteCourseMain() as AppCompatButton

        btnDeleteCourseMain.isVisible = true
        btnDeleteCourseMain.isEnabled = true

        btnDeleteCourseMain.setOnClickListener {
            deletingDialog()
        }
    }

    private fun deletingDialog() {
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


    @SuppressLint("SetTextI18n")
    private fun editCourse() {
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
        showDeleteCourseBtn()
        binding.tvTotalCourseCount.text = "코스 선택"
        binding.btnEditCourse.text = "취소"
        isSelectAvailable = true
    }

    private fun exitEditMode() {
        availableEdit = false
        isSelectAvailable = false
        if (::storageMyDrawAdapter.isInitialized) {

            storageMyDrawAdapter.clearSelection()
            storageMyDrawAdapter.handleCheckBoxVisibility(
                false
            )
        }
        binding.btnEditCourse.text = "편집"
        binding.tvTotalCourseCount.text = "총 코스 ${viewModel.myDrawCourses.size}개"

        viewModel.clearItemsToDelete()
        hideDeleteCourseBtn()
        showBottomNav()
    }

    private fun observeDeleteState() {
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
        binding.indeterminateBar.isVisible = false
        storageMyDrawAdapter.removeItems(viewModel.itemsToDelete)
        storageMyDrawAdapter.clearSelection()
        viewModel.clearItemsToDelete()
    }

    private fun handleUnsuccessfulUploadCall() {
        binding.indeterminateBar.isVisible = false
        Timber.tag(ContentValues.TAG).d("Failure : ${viewModel.errorMessage.value}")
    }

    fun deleteCourse() {
        viewModel.deleteMyDrawCourse()
        binding.btnEditCourse.text = "편집"
    }

    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    @SuppressLint("SetTextI18n")
    private fun showMyDrawResult() {
        Timber.tag(ContentValues.TAG)
            .d("Success but emptyList : ${viewModel.myDrawCourses.isEmpty()}")
        if (viewModel.myDrawCourses.isEmpty()) {
            showEmptyView()
        } else {
            hideEmptyView()
        }
    }

    private fun updateAdapterData() {
        storageMyDrawAdapter.submitList(viewModel.myDrawCourses)
    }

    private fun addObserver() {
        observeStorageState()
        manageSaveDeleteBtnCondition()
        observeDeleteState()
        observeItemSize()
    }

    @SuppressLint("SetTextI18n")
    private fun observeItemSize() {
        viewModel.myDrawSize.observe(viewLifecycleOwner) {
            if (viewModel.myDrawSize.value == 0) {
                showEmptyView()
            } else {
                hideEmptyView()
            }
            binding.tvTotalCourseCount.text = "총 코스 ${viewModel.myDrawSize.value}개"
        }
    }

    private fun showEmptyView() {
        with(binding) {
            layoutMyDrawNoCourse.isVisible = true
            recyclerViewStorageMyDraw.isVisible = false
            btnEditCourse.isEnabled = false
            btnEditCourse.isVisible = false
            tvTotalCourseCount.isVisible = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun hideEmptyView() {
        with(binding) {
            layoutMyDrawNoCourse.isVisible = false
            recyclerViewStorageMyDraw.isVisible = true
            btnEditCourse.isEnabled = true
            btnEditCourse.isVisible = true
            tvTotalCourseCount.isVisible = true
            binding.tvTotalCourseCount.text = "총 코스 ${viewModel.myDrawCourses.size}개"
        }
    }

    private fun observeStorageState() {
        viewModel.storageState.observe(viewLifecycleOwner) {
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

    private fun requireCourse() {
        binding.btnStorageNoCourse.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("root", "storageNoScrap")
            startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }

    private fun getCourse() {
        viewModel.getMyDrawList()
    }


    private fun manageSaveDeleteBtnCondition() {
        viewModel.itemsToDeleteLiveData.observe(viewLifecycleOwner) {
            val selectedItems = viewModel.itemsToDeleteLiveData.value!!.size
            if (selectedItems == 0) {
                disableSaveBtn()
            } else {
                if (selectedItems > 0) {
                    enableDeleteBtn()
                    btnDeleteCourseMain.text = "삭제하기(${selectedItems})"
                } else {
                    btnDeleteCourseMain.text = "삭제하기"
                }
            }
        }
    }

    private fun disableSaveBtn() {
        btnDeleteCourseMain =
            mainActivity.getBtnDeleteCourseMain() as AppCompatButton

        btnDeleteCourseMain.text = "완료하기"
        btnDeleteCourseMain.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.G3))
        btnDeleteCourseMain.isEnabled = false
    }

    private fun enableDeleteBtn() {
        btnDeleteCourseMain =
            mainActivity.getBtnDeleteCourseMain() as AppCompatButton

        btnDeleteCourseMain.text = "삭제하기"
        btnDeleteCourseMain.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.M1))
        btnDeleteCourseMain.isEnabled = true
    }

    override fun selectItem(id: Int): Boolean {
        Timber.tag(ContentValues.TAG).d("코스 아이디 : $id")

        return if (!isSelectAvailable) {
            val intent = Intent(activity, MyDrawDetailActivity::class.java)
            intent.putExtra("fromStorageFragment", id)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            false
        } else {
            viewModel.modifyItemsToDelete(id)
            true
        }
    }

    override fun calcItemSize(itemCount: Int) {
        viewModel.myDrawSize.value = itemCount
    }
}






