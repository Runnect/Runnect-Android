package com.runnect.runnect.presentation.storage

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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

    private lateinit var animDown: Animation
    private lateinit var animUp: Animation

//    lateinit var btnDeleteCourseMain: AppCompatButton
//    lateinit var btmNaviMain: BottomNavigationView


    var isSelectAvailable = false


    private fun editCourse() {
        binding.btnEditCourse.setOnClickListener {
            //여기서 체크박스 visible로 바꾸고 터치 여부에 따라 setDrawble만 바뀌게
            //참조로 가져와야 함.
            hideBtmNavi()
            showDeleteCourseBtn()
            // 총코스 TextView -> 코스 선택
            // btnEditCourse.isVisible = false
            isSelectAvailable = true
        }
    }

    fun hideBtmNavi() {
        animDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_out_down)

        val btmNaviMain = mainActivity.getBtmNaviMain() as BottomNavigationView

        //Bottom invisible
        btmNaviMain.startAnimation(animDown)
        btmNaviMain.isVisible = false
    }

    fun showBtmNavi() {
        animUp = AnimationUtils.loadAnimation(context, R.anim.slide_out_up)

        val btmNaviMain = mainActivity.getBtmNaviMain() as BottomNavigationView

        //Bottom visible
        btmNaviMain.startAnimation(animUp)
        btmNaviMain.isVisible = true
    }

    private fun hideDeleteCourseBtn() {
        animDown = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_out_down)

        val btnDeleteCourseMain = mainActivity.getBtnDeleteCourseMain() as AppCompatButton

//        btnDeleteCourseMain.startAnimation(animDown)

        btnDeleteCourseMain.isVisible = false //default false
        //item 터치가 하나 이상될 시 버튼 활성화, 아니면 다시 비활성화

        btnDeleteCourseMain.isEnabled = false //이 부분은 없어도 될 것 같긴 한다ㅔ


    }

    private fun showDeleteCourseBtn() {
        animUp = AnimationUtils.loadAnimation(context, R.anim.slide_out_up)

        val btnDeleteCourseMain = mainActivity.getBtnDeleteCourseMain() as AppCompatButton

//        btmDeleteCourseMain?.startAnimation(animUp)
        btnDeleteCourseMain.isVisible = true //default false
        //item 터치가 하나 이상될 시 버튼 활성화, 아니면 다시 비활성화

        btnDeleteCourseMain.isEnabled = true //default false

        btnDeleteCourseMain.setOnClickListener { //이 부분 나중에 따로 함수로 빼주기
            customDialog(binding.root)
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
            Timber.d("selectionTracker.selection 값???? ${selectionTracker.selection.size()}") //여긴 문제 x 삭제후 다시 2개 선택해도 2개 뜸
            storageMyDrawAdapter.removeItem(selectionTracker.selection)
            selectionTracker.clearSelection()
            Timber.d("selectionTracker.selection 후 값???? ${selectionTracker.selection.size()}") //인덱스값 0,3반환
            dialog.dismiss()

//            hideDeleteCourseBtn()
//            showBtmNavi()
            //삭제하기 누르면 다시 총코스 TextView랑 btnEditCourse.isVisible = true
        }
        myLayout.btn_delete_no.setOnClickListener {
            dialog.dismiss()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        binding.lifecycleOwner = requireActivity()

        initAdapter()
        editCourse()
        getCourse() //문제 없음
        addData() //상관 없음
        addObserver()
        addTrackerObserver() //selection
    }


    private fun initAdapter() {
        storageMyDrawAdapter = StorageMyDrawAdapter(this)
        binding.recyclerViewStorageMyDraw.adapter = storageMyDrawAdapter
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
                } //data 수신에는 성공했지만 onSuccess에서 예외가 발생해서 onFailure로 넘어간 것 같음

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
            setSelectionTracker("StorageMyDrawSelectionTracker", binding.recyclerViewStorageMyDraw)
        selectionTracker.addObserver((object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (!isSelectAvailable) {
                    selectionTracker.clearSelection()
                } //터치 활성화 여부
                val items = selectionTracker.selection.size()
                Log.d(ContentValues.TAG, "items 사이즈?: $items")
                if (items == 0) {
                    disableSaveBtn()
                } else if (items >= 1) {
                    enableDeleteBtn()
                } // 선택된 아이템이 1개 이상일 경우 button 활성화 (floating button하고 그냥 버튼하고 기본 지원 옵션이 좀 다른 듯함.)
            }
        }))
        storageMyDrawAdapter.setSelectionTracker(selectionTracker) //어댑터 생성 후 할당해줘야 한다는 순서지킴
    }

    private fun enableDeleteBtn() {
        val btnDeleteCourseMain = mainActivity.getBtnDeleteCourseMain() as AppCompatButton

        btnDeleteCourseMain.text = "삭제하기"
        btnDeleteCourseMain.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.M1))
        btnDeleteCourseMain.isEnabled = true
    }

    private fun disableSaveBtn() {
        val btnDeleteCourseMain = mainActivity.getBtnDeleteCourseMain() as AppCompatButton

        btnDeleteCourseMain.text = "완료하기"
        btnDeleteCourseMain.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.G3))
        btnDeleteCourseMain.isEnabled = false
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






