package com.runnect.runnect.presentation.mydrawdetail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.model.MyDrawToRunData
import com.runnect.runnect.databinding.ActivityMyDrawDetailBinding
import com.runnect.runnect.presentation.countdown.CountDownActivity
import timber.log.Timber

class MyDrawDetailActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityMyDrawDetailBinding>(R.layout.activity_my_draw_detail) {


    val viewModel: MyDrawDetailViewModel by viewModels()
    val service = KApiCourse.ServicePool.courseService //객체 생성

    lateinit var startLatLng: LatLng
    private val touchList = arrayListOf<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding.model = viewModel
        binding.lifecycleOwner = this
        getMyDrawDetail()
        observing()
        toCountDownButton()

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun getMyDrawDetail() {
        val courseId = intent.getIntExtra("fromStorageFragment", 0)
        Timber.tag(ContentValues.TAG).d("courseId from Storage : $courseId")
        viewModel.getMyDrawDetail(courseId = courseId) //서버통신
    }

    fun toCountDownButton() {
        binding.btnMyDrawDetailRun.setOnClickListener {
            startActivity(Intent(this, CountDownActivity::class.java).apply {

                putExtra("myDrawToRun", viewModel.myDrawToRunData.value)

            })
        }
    }

    fun observing() {
        viewModel.errorMessage.observe(this) {
            //실패 시 action
        }
        viewModel.getResult.observe(this) {

            with(binding) {
                Glide
                    .with(ivMyDrawDetail.context)
                    .load(it.data.course.image.toUri())
                    .centerCrop()
                    .into(ivMyDrawDetail)

                tvCourseDistanceRecord.text = it.data.course.distance.toString()

            }

            startLatLng =
                LatLng(it.data.course.path[0][0], it.data.course.path[0][1]) //startLatLng에 값 할당
            Timber.tag(ContentValues.TAG).d("startLatLng 값 : $startLatLng")

            //val touchList = it.data.course.path as ArrayList<LatLng>
            //touchList.removeAt(0)
            //이렇게 하니까 CountDownActivity로 안 넘어감.
            //근데 arrayListOf()로 만들어주니까 넘어감.
            //정확한 이유가 뭐지


            for (i in 1 until it.data.course.path.size) {
                touchList.add(LatLng(it.data.course.path[i][0], it.data.course.path[i][1]))
            }

            viewModel.myDrawToRunData.value = MyDrawToRunData(
                it.data.course.id,
                publicCourseId = null,
                touchList,
                startLatLng,
                it.data.course.distance,
                it.data.course.departure.name,
                it.data.course.image
            )

            Timber.tag(ContentValues.TAG)
                .d("viewModel.myDrawToRunData.value 값 : ${viewModel.myDrawToRunData.value}")
        }
    }


}