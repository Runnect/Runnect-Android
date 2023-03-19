package com.runnect.runnect.presentation.storage

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import timber.log.Timber


class StorageMyDrawFragment :
    BindingFragment<FragmentStorageMyDrawBinding>(R.layout.fragment_storage_my_draw), OnMyDrawClick {

    val viewModel: StorageViewModel by viewModels()

    lateinit var recyclerviewStorageMyDraw: RecyclerView
    lateinit var selectionTracker: SelectionTracker<Long>
    lateinit var storageMyDrawAdapter : StorageMyDrawAdapter

    //MainActivity에 작성해놓은 메서드 호출
   private var mainActivity: MainActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    fun callMainActivityMethod() {
        mainActivity?.hideBtmNavi()
    }

//    private fun hideBtmNavi(){
//        binding.imgBtnHideBtmNavi.setOnClickListener {
//            callMainActivityMethod()
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        binding.lifecycleOwner = requireActivity()

        recyclerviewStorageMyDraw = binding.recyclerViewStorageMyDraw //initRecyclerView

        getCourse()
        initAdapter()
        addData()
        addObserver()
        addTrackerObserver() //selection
//        hideBtmNavi()

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

    private fun showLoadingBar() {
        binding.indeterminateBar.isVisible = true
    }

    private fun hideLoadingBar() {
        binding.indeterminateBar.isVisible = false
    }

    private fun showMyDrawResult(){
        if (viewModel.getMyDrawResult.value!!.data.courses.isEmpty()) {
            with(binding) {
                ivStorageMyDrawNoCourse.isVisible = true
                tvStorageMyDrawNoCourseGuide.isVisible = true
                btnStorageNoCourse.isVisible = true
                recyclerViewStorageMyDraw.isVisible = false
            }
        } else {
            with(binding) {
                ivStorageMyDrawNoCourse.isVisible = false
                tvStorageMyDrawNoCourseGuide.isVisible = false
                btnStorageNoCourse.isVisible = false
                recyclerViewStorageMyDraw.isVisible = true
            }
        }
    }

    private fun updateAdapterData(){
        storageMyDrawAdapter.submitList(viewModel.getMyDrawResult.value!!.data.courses)
    }


    private fun addObserver() {

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

    private fun addData() {
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

    //SelectionTracker.kt로 빼준 함수 활용
    private fun addTrackerObserver() {
        selectionTracker = setSelectionTracker("StorageMyDrawSelectionTracker", recyclerviewStorageMyDraw)
        selectionTracker.addObserver((object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                val items = selectionTracker.selection.size()
//                if(items == 0) binding.enabled = false
//                else binding.enabled = items >= 1 // 선택된 아이템이 1개 이상일 경우 floating button 활성화

            }
        }))
        storageMyDrawAdapter.setSelectionTracker(selectionTracker) //어댑터 생성 후 할당해줘야 한다는 순서지킴
    }

    private fun initAdapter() {
        storageMyDrawAdapter = StorageMyDrawAdapter(this).apply {
            submitList(viewModel.getMyDrawResult.value!!.data.courses)
        }
        recyclerviewStorageMyDraw.adapter = storageMyDrawAdapter
    }

    override fun selectItem(item: ResponseGetCourseDto.Data.Course) {
        Timber.tag(ContentValues.TAG).d("코스 아이디 : ${item.id}")
        startActivity(Intent(activity, MyDrawDetailActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            putExtra("fromStorageFragment", item.id)
        })
    }
}






