package com.runnect.runnect.presentation.storage.mydrawdetail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetail
import com.runnect.runnect.databinding.ActivityMyDrawDetailBinding
import com.runnect.runnect.databinding.LayoutCommonToolbarBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.util.custom.toolbar.CommonToolbarLayout
import com.runnect.runnect.util.custom.toolbar.ToolbarMenu
import com.runnect.runnect.util.extension.PermissionUtil
import com.runnect.runnect.util.extension.navigateToPreviousScreenWithAnimation
import com.runnect.runnect.util.extension.setActivityDialog
import com.runnect.runnect.util.extension.showSnackbar
import com.runnect.runnect.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.view.btn_delete_no
import kotlinx.android.synthetic.main.custom_dialog_delete.view.btn_delete_yes
import timber.log.Timber

@AndroidEntryPoint
class MyDrawDetailActivity :
    BindingActivity<ActivityMyDrawDetailBinding>(R.layout.activity_my_draw_detail),
    CommonToolbarLayout {
    val viewModel: MyDrawDetailViewModel by viewModels()
    private val selectList = arrayListOf<Int>()
    private val touchList = arrayListOf<LatLng>()
    private lateinit var departureLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        getMyDrawDetail()
        addListener()
        addObserver()
        registerBackPressedCallback()
    }

    private fun getMyDrawDetail() {
        val courseId = intent.getIntExtra(EXTRA_COURSE_ID, 0)
        if (courseId == 0) {
            showSnackbar(
                anchorView = binding.root,
                message = "fail get my draw course detail"
            )
            return
        }

        selectList.add(courseId)
        viewModel.getMyDrawDetail(courseId)
    }

    private fun addListener() {
        initBackButtonClickListener()
        initDeleteButtonClickListener()
        initDrawButtonClickListener()
    }

    private fun initBackButtonClickListener() {
        binding.imgBtnBack.setOnClickListener {
            navigateToPreviousScreenWithAnimation()
        }
    }

    private fun initDeleteButtonClickListener() {
        binding.imgBtnDelete.setOnClickListener {
            showDeleteConfirmDialog()
        }
    }

    private fun showDeleteConfirmDialog() {
        val (dialog, dialogLayout) = setActivityDialog(
            layoutInflater = layoutInflater,
            view = binding.root,
            resId = R.layout.custom_dialog_delete,
            cancel = true
        )
        with(dialogLayout) {
            this.btn_delete_yes.setOnClickListener {
                deleteCourse()
                dialog.dismiss()
                val intent = Intent(this@MyDrawDetailActivity, MainActivity::class.java).apply {
                    putExtra(EXTRA_FRAGMENT_REPLACEMENT_DIRECTION, "fromDeleteMyDrawDetail")
                }
                startActivity(intent)
                navigateToPreviousScreenWithAnimation()
            }
            this.btn_delete_no.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun deleteCourse() {
        viewModel.deleteMyDrawCourse(selectList)
    }

    private fun initDrawButtonClickListener() {
        binding.btnMyDrawDetailRun.setOnClickListener {
            this.let {
                PermissionUtil.requestLocationPermission(
                    context = it,
                    onPermissionGranted = { navigateToCountDown() },
                    onPermissionDenied = { showPermissionDeniedToast() },
                    permissionType = PermissionUtil.PermissionType.LOCATION
                )
            }
        }
    }

    private fun showPermissionDeniedToast() {
        showToast(getString(R.string.location_permission_denied))
    }

    private fun navigateToCountDown() {
        startActivity(Intent(this, CountDownActivity::class.java).apply {
            putExtra(EXTRA_COURSE_DATA, viewModel.myDrawToRunData.value)
        })
    }

    fun addObserver() {
        observeMyDrawCourseGetResult()
    }

    private fun observeMyDrawCourseGetResult() {
        viewModel.getResult.observe(this) { response ->
            setImage(response) //이거 그냥 BindingAdapter로 빼도 되는데 지금 xml에서 뷰모델이 아닌 dto를 바로 구독하고 있어서 이러는 것 같음
            setDepartureLatLng(response)
            setTouchList(response)
            setPutExtraValue(response)
        }
    }

    private fun setImage(src: ResponseGetMyDrawDetail) {
        with(binding) {
            Glide
                .with(ivMyDrawDetail.context)
                .load(src.data.course.image.toUri())
                .centerCrop()
                .into(ivMyDrawDetail)

            tvCourseDistanceRecord.text = src.data.course.distance.toString()

        }
    }

    private fun setDepartureLatLng(src: ResponseGetMyDrawDetail) {
        departureLatLng =
            LatLng(src.data.course.path[0][0], src.data.course.path[0][1])
        Timber.tag(ContentValues.TAG).d("departureLatLng 값 : $departureLatLng")
    }

    private fun setTouchList(src: ResponseGetMyDrawDetail) {
        for (i in 1 until src.data.course.path.size) {
            touchList.add(LatLng(src.data.course.path[i][0], src.data.course.path[i][1]))
        }
    }

    private fun setPutExtraValue(src: ResponseGetMyDrawDetail) {
        viewModel.myDrawToRunData.value = CourseData(
            courseId = src.data.course.id,
            publicCourseId = null,
            touchList = touchList,
            startLatLng = departureLatLng,
            departure = src.data.course.departure.name,
            distance = src.data.course.distance,
            image = src.data.course.image,
            dataFrom = "fromMyDrawDetail"
        )
    }

    private fun registerBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToPreviousScreenWithAnimation()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override val toolbarBinding: LayoutCommonToolbarBinding
        get() = binding.layoutCommonToolbar

    override fun initToolBarLayout() {
        addMenuTo(
            CommonToolbarLayout.LEFT,
            ToolbarMenu.Icon(
                resourceId = R.drawable.all_back_arrow,
                clickEvent = {
                    // todo: 공유 링크 타고 들어왔을 때는
                    //  뒤로가기 누르면 보관함 프래그먼트로 이동
                    navigateToPreviousScreenWithAnimation()
                }
            ),
            ToolbarMenu.Text(
                resourceId =
                /** 내가 그린 코스 제목 */
                ,
                padding = 0,
                textSize = 18,
                fontRes = R.font.pretendard_bold
            )
        )
    }

    companion object {
        const val EXTRA_FRAGMENT_REPLACEMENT_DIRECTION = "fragmentReplacementDirection"
        const val EXTRA_COURSE_DATA = "CourseData"
        const val EXTRA_COURSE_ID = "courseId"
    }
}