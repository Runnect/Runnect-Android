package com.runnect.runnect.presentation.storage

import android.content.ContentValues
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
import com.runnect.runnect.databinding.FragmentStorageMyDrawBinding
import com.runnect.runnect.presentation.mydrawdetail.MyDrawDetailActivity
import com.runnect.runnect.presentation.search.SearchActivity
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.storage.adapter.StorageMyDrawAdapter
import com.runnect.runnect.presentation.storage.adapter.setSelectionTracker
import com.runnect.runnect.util.GridSpacingItemDecoration
import timber.log.Timber


class StorageMyDrawFragment :
    BindingFragment<FragmentStorageMyDrawBinding>(R.layout.fragment_storage_my_draw) {

    val viewModel: StorageViewModel by viewModels()

    lateinit var recyclerviewStorage: RecyclerView
    lateinit var selectionTracker: SelectionTracker<Long>

    //이거 인터페이스로 빼자
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

        recyclerviewStorage = binding.recyclerViewStorageMyDraw //initRecyclerView
        recyclerviewStorage.adapter = storageMyDrawAdapter //initAdapter

        getCourse()
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


    private fun addObserver() {

        viewModel.storageState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.Empty -> binding.indeterminateBar.isVisible = false //visible 옵션으로 처리하는 게 맞나
                UiState.Loading -> binding.indeterminateBar.isVisible = true
                UiState.Success -> {
                    binding.indeterminateBar.isVisible = false

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
                    storageMyDrawAdapter.submitList(viewModel.getMyDrawResult.value!!.data.courses)
                }
                UiState.Failure -> {
                    binding.indeterminateBar.isVisible = false
                    Timber.tag(ContentValues.TAG)
                        .d("Success : getSearchList body is not null")
                }

            }

        }

    }

    private fun addData() {
        binding.btnStorageNoCourse.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) //페이지 전환 시 애니메이션 제거
            }
            startActivity(intent)
        }
    }

    private fun getCourse() {
        viewModel.getMyDrawList()
    }

    //SelectionTracker.kt로 빼준 함수 활용
    private fun addTrackerObserver() {
        selectionTracker = setSelectionTracker("StorageMyDrawSelectionTracker", recyclerviewStorage)
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
}

//    private var mainActivity: MainActivity? = null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mainActivity = context as MainActivity
//    }
//
//    // MainActivity의 메서드를 호출하는 예시 메서드
//    fun callMainActivityMethod() {
//        mainActivity?.hideBtmNavi()
//    }
//
//    private fun hideBtmNavi(){
//        binding.imgBtnHideBtmNavi.setOnClickListener {
//            callMainActivityMethod()
//        }
//    }




