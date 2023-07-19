package com.runnect.runnect.presentation.countdown

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.databinding.ActivityCountDownBinding
import com.runnect.runnect.presentation.run.RunActivity

class CountDownActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {

    lateinit var courseData: CourseData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseData = intent.getParcelableExtra("CourseData")!!

        val intentToRun = Intent(this, RunActivity::class.java)
        val numList = arrayListOf(
            AppCompatResources.getDrawable(this, R.drawable.anim_num2),
            AppCompatResources.getDrawable(this, R.drawable.anim_num1)
        )
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_count)
        setAnimationListener(anim, numList, intentToRun)
        binding.ivCountDown.startAnimation(anim)
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
                        putExtra(
                            "CountToRunData", CourseData(
                                courseId = courseData.courseId,
                                publicCourseId = courseData.publicCourseId,
                                touchList = courseData.touchList,
                                startLatLng = courseData.startLatLng,
                                departure = courseData.departure,
                                distance = courseData.distance,
                                image = courseData.image,
                                dataFrom = courseData.dataFrom
                            )
                        )
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