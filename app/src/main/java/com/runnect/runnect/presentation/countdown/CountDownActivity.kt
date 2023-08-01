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

        courseData = intent.getParcelableExtra(EXTRA_COURSE_DATA)!!

        val intentToRun = Intent(this, RunActivity::class.java)
        val numList = arrayListOf(
            AppCompatResources.getDrawable(this, R.drawable.anim_num1),
            AppCompatResources.getDrawable(this, R.drawable.anim_num2)
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
        var counter = COUNT_START

        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                counter -= COUNT_DECREASE_UNIT
                if (counter == COUNT_END) {
                    intentToRun.apply {
                        putExtra(
                            EXTRA_COUNTDOWN_TO_RUN, CourseData(
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

    companion object {
        const val COUNT_START = 2
        const val COUNT_END = -1
        const val COUNT_DECREASE_UNIT = 1
        const val EXTRA_COUNTDOWN_TO_RUN = "CountToRunData"
        const val EXTRA_COURSE_DATA = "CourseData"
    }
}