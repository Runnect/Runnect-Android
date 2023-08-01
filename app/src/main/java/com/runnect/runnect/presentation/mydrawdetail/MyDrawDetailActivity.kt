package com.runnect.runnect.presentation.mydrawdetail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.response.ResponseGetMyDrawDetailDto
import com.runnect.runnect.databinding.ActivityMyDrawDetailBinding
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.countdown.CountDownActivity
import com.runnect.runnect.util.extension.setActivityDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_dialog_delete.view.btn_delete_no
import kotlinx.android.synthetic.main.custom_dialog_delete.view.btn_delete_yes
import timber.log.Timber

@AndroidEntryPoint
class MyDrawDetailActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityMyDrawDetailBinding>(R.layout.activity_my_draw_detail) {


    val viewModel: MyDrawDetailViewModel by viewModels()

    val selectList = arrayListOf<Int>()

    lateinit var departureLatLng: LatLng
    private val touchList = arrayListOf<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        getMyDrawDetail()
        backButton()
        addObserver()
        toCountDownButton()
        deleteButton()
    }

    private fun deletingDialog() {
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
                    putExtra(EXTRA_FRAGMENT_REPLACE_DIRECTION, true)
                }
                startActivity(intent)
            }
            this.btn_delete_no.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun deleteButton() {
        binding.imgBtnDelete.setOnClickListener {
            deletingDialog()
        }
    }

    private fun backButton() { //png가 imgBtn으로 하면 잘리길래 어차피 임시로 해놓는 거니까 imgView로 component를 추가해줬음
        binding.imgBtnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun getMyDrawDetail() {
        val courseId = intent.getIntExtra(EXTRA_COURSE_ID, 0)
        Timber.tag(ContentValues.TAG).d("courseId from Storage : $courseId")

        selectList.add(courseId) //courseId를 지역변수로 선언해서 여기에 작성해주었음
        viewModel.getMyDrawDetail(courseId = courseId)
    }

    fun toCountDownButton() {
        binding.btnMyDrawDetailRun.setOnClickListener {
            startActivity(Intent(this, CountDownActivity::class.java).apply {
                putExtra(EXTRA_COURSE_DATA, viewModel.myDrawToRunData.value)
            })
        }
    }

    fun addObserver() {
        observeGetResult()
    }

    private fun setImage(src: ResponseGetMyDrawDetailDto) {
        with(binding) {
            Glide
                .with(ivMyDrawDetail.context)
                .load(src.data.course.image.toUri())
                .centerCrop()
                .into(ivMyDrawDetail)

            tvCourseDistanceRecord.text = src.data.course.distance.toString()

        }
    }

    //set이란 단어가 표현력이 떨어지는 것 같기도 하고. 그래서 일단 뭉탱이로 두는 것보단 쪼개는 게 나아서 쪼개놓음
    private fun setDepartureLatLng(src: ResponseGetMyDrawDetailDto) {
        departureLatLng =
            LatLng(src.data.course.path[0][0], src.data.course.path[0][1])
        Timber.tag(ContentValues.TAG).d("departureLatLng 값 : $departureLatLng")
    }

    private fun setTouchList(src: ResponseGetMyDrawDetailDto) {
        for (i in 1 until src.data.course.path.size) {
            touchList.add(LatLng(src.data.course.path[i][0], src.data.course.path[i][1]))
        }
    }

    private fun setPutExtraValue(src: ResponseGetMyDrawDetailDto) {
        viewModel.myDrawToRunData.value = CourseData(
            courseId = src.data.course.id,
            publicCourseId = null,
            touchList = touchList,
            startLatLng = departureLatLng,
            departure = src.data.course.departure.name,
            distance = src.data.course.distance,
            image = src.data.course.image,
            dataFrom = "myDraw"
        )
    }

    private fun observeGetResult() {
        viewModel.getResult.observe(this) {
            setImage(it) //이거 그냥 BindingAdapter로 빼도 되는데 지금 xml에서 뷰모델이 아닌 dto를 바로 구독하고 있어서 이러는 것 같음
            setDepartureLatLng(it)
            setTouchList(it)
            setPutExtraValue(it)
        }
    }

    fun deleteCourse() {
        viewModel.deleteMyDrawCourse(selectList)
    }

    companion object {
        const val EXTRA_FRAGMENT_REPLACE_DIRECTION = "fromDeleteMyDraw"
        const val EXTRA_COURSE_DATA = "CourseData"
        const val EXTRA_COURSE_ID = "fromStorageFragment"
    }


}