package com.runnect.runnect.presentation.storage

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import kotlinx.android.synthetic.main.custom_dialog_delete.view.*
import timber.log.Timber


class StorageMyDrawFragment :
    BindingFragment<FragmentStorageMyDrawBinding>(R.layout.fragment_storage_my_draw),
    OnMyDrawClick {

    val viewModel: StorageViewModel by viewModels()

    lateinit var recyclerviewStorageMyDraw: RecyclerView
    lateinit var selectionTracker: SelectionTracker<Long>
    private val storageMyDrawAdapter = StorageMyDrawAdapter(this)


    private lateinit var animDown: Animation
    private lateinit var animUp: Animation

    //MainActivity에 작성해놓은 메서드 호출
    private var mainActivity: MainActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    fun callMainActivityMethod() {
        mainActivity?.hideBtmNavi()
    }

    private fun hideBtmNavi() {
        binding.btnEditCourse.setOnClickListener {
            callMainActivityMethod()
            // 총코스 TextView -> 코스 선택
            // btnEditCourse.isVisible = false
        }
    }

    private fun showDeleteCourseBtn() {
        animUp = AnimationUtils.loadAnimation(context, R.anim.slide_out_up)

        with(binding) {
            btnDeleteCourse.startAnimation(animUp)
            btnDeleteCourse.isVisible = true //default false
            //item 터치가 하나 이상될 시 버튼 활성화, 아니면 다시 비활성화

        }
    }

    fun customDialog(view: View) {
        val myLayout = layoutInflater.inflate(R.layout.custom_dialog_delete, null)

        val build = AlertDialog.Builder(view.context).apply {
            setView(myLayout)
        }
        val dialog = build.create()
//        dialog.setCancelable(false) // 외부 영역 터치 금지
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 내가 짠 layout 외의 영역 투명 처리
        dialog.show()

        myLayout.btn_delete_yes.setOnClickListener {
            dialog.dismiss()
            //삭제하기 누르면 다시 총코스 TextView랑 btnEditCourse.isVisible = true
        }
        myLayout.btn_delete_no.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun deleteCourseBtn() {
        binding.btnDeleteCourse.setOnClickListener {
            customDialog(binding.root)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        binding.lifecycleOwner = requireActivity()

        recyclerviewStorageMyDraw = binding.recyclerViewStorageMyDraw //initRecyclerView
        recyclerviewStorageMyDraw.adapter = storageMyDrawAdapter

        getCourse() //문제 없음
        addData() //상관 없음
        addObserver()
        addTrackerObserver() //selection

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

    private fun showMyDrawResult() {
        Timber.tag(ContentValues.TAG)
            .d("통신 결과 코스 비어있나? : ${viewModel.getMyDrawResult.value!!.data.courses.isEmpty()}")
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

    private fun updateAdapterData() {
        Timber.tag(ContentValues.TAG)
            .d("updateAdapter에 넣은 data : ${viewModel.getMyDrawResult.value!!.data.courses}")
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
                } //뭐지? 조회 성공이 되는데 왜 Failure로 넘어갔지?

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
        selectionTracker =
            setSelectionTracker("StorageMyDrawSelectionTracker", recyclerviewStorageMyDraw)
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


    override fun selectItem(item: ResponseGetCourseDto.Data.Course) {
        Timber.tag(ContentValues.TAG).d("코스 아이디 : ${item.id}")
        startActivity(Intent(activity, MyDrawDetailActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            putExtra("fromStorageFragment", item.id)
        })
    }
}






