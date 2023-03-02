package com.runnect.runnect.presentation.mydrawdetail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.runnect.runnect.R
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.data.model.DetailToRunData
import com.runnect.runnect.databinding.ActivityMyDrawDetailBinding
import com.runnect.runnect.presentation.run.RunActivity
import timber.log.Timber

class MyDrawDetailActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityMyDrawDetailBinding>(R.layout.activity_my_draw_detail) {

    val viewModel: MyDrawDetailViewModel by viewModels()
    val service = KApiCourse.ServicePool.courseService //객체 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding.model = viewModel
        binding.lifecycleOwner = this
        getMyDrawDetail()
        observing()
        toRunButton()

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    fun getMyDrawDetail() {
        val courseId = intent.getIntExtra("fromStorageFragment", 0)
        Timber.tag(ContentValues.TAG).d("courseId from Storage : $courseId")
        viewModel.getMyDrawDetail(courseId = courseId) //서버통신
    }

    fun toRunButton() {
        binding.btnMyDrawDetailRun.setOnClickListener {
            startActivity(Intent(this, RunActivity::class.java).apply {

                putExtra("detailToRun", viewModel.detailToRunData.value)

            })
        }
    }

    fun observing() {
        viewModel.errorMessage.observe(this) {
            //실패 시 action
        }
        viewModel.getResult.observe(this) {
            //여기서 ui 세팅하거나 데이터 바인딩
            //뷰모델에 넣는 게 좋을까? 여기서 Run으로 넘겨야 되는데 재활용되는 뷰는 아님
            //어떤 이미지는 ImageView에 세팅이 되고 어떤 건 안 되는데 원인 파악이 필요함


            with(binding) {
                Glide
                    .with(ivMyDrawDetail.context)
                    .load(it.data.course.image.toUri())
                    .centerCrop()
                    .into(ivMyDrawDetail)

                tvCourseDistanceRecord.text = it.data.course.distance.toString()

            }

            viewModel.detailToRunData.value = DetailToRunData(it.data.course.id,
                publicCourseId = null,
                it.data.course.departure.name,
                it.data.course.distance,
                it.data.course.path,
                it.data.course.image)
        }
    }


}