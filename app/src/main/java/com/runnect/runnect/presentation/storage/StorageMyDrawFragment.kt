package com.runnect.runnect.presentation.storage

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingFragment
import com.runnect.runnect.data.model.ResponseGetCourseDto
import com.runnect.runnect.databinding.FragmentStorageMyDrawBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.mydrawdetail.MyDrawDetailActivity
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.storage.adapter.StorageMyDrawAdapter
import com.runnect.runnect.presentation.storage.adapter.setSelectionTracker
import com.runnect.runnect.util.GridSpacingItemDecoration
import com.runnect.runnect.util.callback.OnMyDrawClick
import kotlinx.android.synthetic.main.custom_dialog_delete.view.*
import timber.log.Timber


class StorageMyDrawFragment :
    BindingFragment<FragmentStorageMyDrawBinding>(R.layout.fragment_storage_my_draw),
    OnMyDrawClick {

    val viewModel: StorageViewModel by viewModels()

    lateinit var selectionTracker: SelectionTracker<Long>
    lateinit var storageMyDrawAdapter: StorageMyDrawAdapter


    private val mainActivity: MainActivity by lazy {
        activity as MainActivity
    } // 이 변수가 최초 할당 이후 계속 MainActivity를 참조하니까 사용을 안 하는 순간에는 null을 할당해줌으로써 메모리에서 내려줘야 함.

    lateinit var btnDeleteCourseMain: AppCompatButton
    lateinit var bottomNavMain: BottomNavigationView

    private lateinit var animDown: Animation
    private lateinit var animUp: Animation


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
        addSelectionTracker()
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
        storageMyDrawAdapter = StorageMyDrawAdapter(this)
        binding.recyclerViewStorageMyDraw.adapter = storageMyDrawAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun editCourse() {
        var availableEdit = false
        binding.btnEditCourse.setOnClickListener {
            if (!availableEdit) {
                availableEdit = true
                hideBottomNav()
                showDeleteCourseBtn()
                binding.tvTotalCourseCount.text = "코스 선택"
                binding.btnEditCourse.text = "취소"
                isSelectAvailable = true
            } else {
                availableEdit = false
                binding.btnEditCourse.text = "편집"
                selectionTracker.clearSelection()
                isSelectAvailable = false
                hideDeleteCourseBtn()
                showBottomNav()
                binding.tvTotalCourseCount.text =
                    "총 코스 ${viewModel.getMyDrawResult.value!!.data.courses.size}개"
            }
        }
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
            customDialog(binding.root)
        }
    }


    fun customDialog(view: View) {
        val myLayout = layoutInflater.inflate(R.layout.custom_dialog_delete, null)

        val build = AlertDialog.Builder(view.context).apply {
            setView(myLayout)
        }
        val dialog = build.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        myLayout.btn_delete_yes.setOnClickListener {
            deleteCourse()
            clearSelection()
            initAdapter()
            addSelectionTracker()
            getMyDrawCourse()
            dialog.dismiss()
            isSelectAvailable = false
            hideDeleteCourseBtn()
            showBottomNav()
        }
        myLayout.btn_delete_no.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun deleteCourse() {
        viewModel.deleteMyDrawCourse(viewModel.selectList.value!!)
        binding.btnEditCourse.text = "편집"
    }

    private fun clearSelection() {
        selectionTracker.clearSelection()
    }

    private fun getMyDrawCourse() {
        viewModel.getMyDrawList()
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
            .d("Success but emptyList : ${viewModel.getMyDrawResult.value!!.data.courses.isEmpty()}")
        if (viewModel.getMyDrawResult.value!!.data.courses.isEmpty()) {
            with(binding) {
                ivStorageMyDrawNoCourse.isVisible = true
                tvStorageMyDrawNoCourseGuide.isVisible = true
                tvStorageMyDrawNoCourseGuide2.isVisible = true
                btnStorageNoCourse.isVisible = true
                recyclerViewStorageMyDraw.isVisible = false
                btnEditCourse.isEnabled = false
                tvTotalCourseCount.text =
                    "총 코스 ${viewModel.getMyDrawResult.value!!.data.courses.size}개"
            }
        } else {
            with(binding) {
                ivStorageMyDrawNoCourse.isVisible = false
                tvStorageMyDrawNoCourseGuide.isVisible = false
                tvStorageMyDrawNoCourseGuide2.isVisible = false
                btnStorageNoCourse.isVisible = false
                recyclerViewStorageMyDraw.isVisible = true
                btnEditCourse.isEnabled = true
                tvTotalCourseCount.text =
                    "총 코스 ${viewModel.getMyDrawResult.value!!.data.courses.size}개"
            }
        }
    }

    private fun updateAdapterData() {
        storageMyDrawAdapter.submitList(viewModel.getMyDrawResult.value!!.data.courses)
    }

    private fun addObserver() {
        observeStorageState()
        observeGetMyDrawResult()
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

    fun observeGetMyDrawResult() {
        viewModel.getMyDrawResult.observe(viewLifecycleOwner) {
            storageMyDrawAdapter.submitList(viewModel.getMyDrawResult.value!!.data.courses)
        }
    }

    private fun requireCourse() {
        binding.btnStorageNoCourse.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            startActivity(intent)
        }
    }

    private fun getCourse() {
        viewModel.getMyDrawList()
    }

    private fun addSelectionTracker() {
        createSelectionTracker()
        setSelectionTrackerObserver()
        setTrackerInAdapter()
    }

    private fun createSelectionTracker() {
        selectionTracker =
            setSelectionTracker("StorageMyDrawSelectionTracker", binding.recyclerViewStorageMyDraw)
    }

    private fun setTrackerInAdapter() {
        storageMyDrawAdapter.setSelectionTracker(selectionTracker)
    }

    private fun setSelectionTrackerObserver() {
        selectionTracker.addObserver((object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                checkDisableSelection()
                createSelectList()
                manageSaveDeleteBtnCondition()
            }
        }))
    }

    private fun checkDisableSelection() {
        if (!isSelectAvailable) {
            selectionTracker.clearSelection()
        }
    }

    private fun createSelectList() {
        viewModel.selectList.value = selectionTracker.selection.toMutableList()
        Timber.tag(ContentValues.TAG)
            .d("실시간 뷰모델 selectList 값 : ${viewModel.selectList.value}")
    }

    private fun manageSaveDeleteBtnCondition() {
        val selectedItems = selectionTracker.selection.size()
        if (selectedItems == 0) {
            disableSaveBtn()
        } else if (selectedItems >= 1) {
            enableDeleteBtn()
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

    override fun selectItem(item: ResponseGetCourseDto.Data.Course) {
        Timber.tag(ContentValues.TAG).d("코스 아이디 : ${item.id}")

        if (!isSelectAvailable) {
            startActivity(Intent(activity, MyDrawDetailActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra("fromStorageFragment", item.id)
            })
        }
    }
}






