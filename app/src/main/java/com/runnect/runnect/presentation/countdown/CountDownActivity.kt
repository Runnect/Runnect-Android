package com.runnect.runnect.presentation.countdown

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import com.runnect.runnect.R
import com.runnect.runnect.data.model.CountToRunData
import com.runnect.runnect.data.model.DetailToRunData
import com.runnect.runnect.data.model.DrawToRunData
import com.runnect.runnect.data.model.MyDrawToRunData
import com.runnect.runnect.databinding.ActivityCountDownBinding
import com.runnect.runnect.presentation.run.RunActivity

class CountDownActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {

    val viewModel: CountDownViewModel by viewModels()

    //myDrawToRunData는 startLatLng이랑 touchList를 미리 쪼개서 보내도록 수정해줬는데
    //detailToRunData는 서버에서 path까지 받아올 수 있게 api를 수정해주고난 다음에야 작업 가능.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setViewModelData()

        val intentToRun = Intent(this, RunActivity::class.java)
        val numList = arrayListOf(
            AppCompatResources.getDrawable(this, R.drawable.anim_num2),
            AppCompatResources.getDrawable(this, R.drawable.anim_num1)
        )
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_count)
        setAnimationListener(anim, numList, intentToRun)
        binding.ivCountDown.startAnimation(anim)
    }

    fun setViewModelData() {

        val drawToRunData: DrawToRunData? = intent.getParcelableExtra("DrawToRunData")
        val detailToRunData: DetailToRunData? = intent.getParcelableExtra("detailToRun")
        val myDrawToRunData: MyDrawToRunData? = intent.getParcelableExtra("myDrawToRun")

        if (myDrawToRunData != null) {
            viewModel.publicCourseId.value = myDrawToRunData.publicCourseId
            viewModel.courseId.value = myDrawToRunData.courseId
            viewModel.departure.value = myDrawToRunData.departure
            viewModel.distanceSum.value = myDrawToRunData.distance.toDouble()
            viewModel.touchList.value = myDrawToRunData.touchList
            viewModel.startLatLng.value = myDrawToRunData.startLatLng
            viewModel.captureUri.value = myDrawToRunData.image
            viewModel.dataFrom.value = "myDraw"
        }

        if (drawToRunData != null) {
            viewModel.publicCourseId.value = drawToRunData.publicCourseId
            viewModel.courseId.value = drawToRunData.courseId
            viewModel.departure.value = drawToRunData.departure
            viewModel.distanceSum.value =
                drawToRunData.totalDistance.toDouble() //딴 데랑 type이 달라 뭐든 통일시켜줘야 할 듯
            viewModel.touchList.value = drawToRunData.touchList
            viewModel.startLatLng.value = drawToRunData.startLatLng
            viewModel.captureUri.value = drawToRunData.captureUri
            viewModel.dataFrom.value = "draw"
        }

        if (detailToRunData != null) {
            viewModel.publicCourseId.value = detailToRunData.publicCourseId
            viewModel.courseId.value = detailToRunData.courseId
            viewModel.departure.value = detailToRunData.departure
            viewModel.distanceSum.value =
                detailToRunData.distance.toDouble() //딴 데랑 type이 달라 뭐든 통일시켜줘야 할 듯
            viewModel.touchList.value = detailToRunData.touchList
            viewModel.startLatLng.value = detailToRunData.startLatLng
            viewModel.captureUri.value = detailToRunData.image
            viewModel.dataFrom.value = "draw"
        }


    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun setAnimationListener(
        anim: Animation,
        numList: ArrayList<Drawable?>,
        intentToRun: Intent,
    ) {
        var counter = -1

        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                counter += 1
                if (counter == 2) {
                    intentToRun.apply {
                        putExtra("CountToRunData",
                            CountToRunData(
                                viewModel.courseId.value,
                                viewModel.publicCourseId.value,
                                viewModel.departure.value!!,
                                viewModel.distanceSum.value!!.toFloat(),
                                viewModel.touchList.value!!,
                                viewModel.startLatLng.value!!,
                                viewModel.captureUri.value!!,
                                viewModel.dataFrom.value!!
                            ))
                    }
                    startActivity(intentToRun)
                    finish()
                } else {
                    binding.ivCountDown.post {
                        binding.ivCountDown.setImageDrawable(numList[counter])
                        binding.ivCountDown.startAnimation(animation)
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}