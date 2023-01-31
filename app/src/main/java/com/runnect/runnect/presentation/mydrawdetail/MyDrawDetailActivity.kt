package com.runnect.runnect.presentation.mydrawdetail

import android.content.ContentValues
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.runnect.runnect.R
import com.runnect.runnect.data.api.KApiCourse
import com.runnect.runnect.databinding.ActivityMyDrawDetailBinding
import timber.log.Timber

class MyDrawDetailActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityMyDrawDetailBinding>(R.layout.activity_my_draw_detail) {

    val viewModel: MyDrawDetailViewModel by viewModels()
    val service = KApiCourse.ServicePool.courseService //객체 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//
//        binding.model = viewModel
        binding.lifecycleOwner = this
        getMyDrawDetail()
        observing()


    }

    fun getMyDrawDetail(){
        val courseId = intent.getIntExtra("fromStorageFragment",0)
        Timber.tag(ContentValues.TAG).d("courseId from Storage : $courseId")
        viewModel.getMyDrawDetail(courseId = courseId) //서버통신


    }

    fun observing(){
        viewModel.errorMessage.observe(this) {
            //실패 시 action
        }
        viewModel.getResult.observe(this) {
            //여기서 ui 세팅하거나 데이터 바인딩
            //뷰모델에 넣는 게 좋을까? 여기서 Run으로 넘겨야 되는데 재활용되는 뷰는 아님

            viewModel.image.value = it.data.course.image.toUri()
            viewModel.distance.value = it.data.course.distance.toDouble()

        }
    }


}