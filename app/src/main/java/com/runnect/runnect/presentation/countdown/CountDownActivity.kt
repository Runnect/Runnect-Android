package com.runnect.runnect.presentation.countdown

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import com.runnect.runnect.R
import com.runnect.runnect.data.model.DrawToRunData
import com.runnect.runnect.databinding.ActivityCountDownBinding
import com.runnect.runnect.presentation.run.RunActivity
import timber.log.Timber

class CountDownActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {

    lateinit var drawToRunData: DrawToRunData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val numList = arrayListOf(
            AppCompatResources.getDrawable(this, R.drawable.anim_num3), //더미
            AppCompatResources.getDrawable(this, R.drawable.anim_num2),
            AppCompatResources.getDrawable(this, R.drawable.anim_num1)
        )
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_count)
        var counter = -1
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                if (counter < 2) {
                    counter += 1
                    Timber.tag("counter").d(counter.toString())
                    binding.isCount.setImageDrawable(numList[counter])
                    binding.isCount.startAnimation(animation)
                }


                if (counter == 2) {
                    drawToRunData = intent.getParcelableExtra("DrawToRunData")!!


                    Timber.tag(ContentValues.TAG).d("drawToRunData : $drawToRunData")

                    //수신 완료

                    val intent = Intent(this@CountDownActivity, RunActivity::class.java).apply {
                        putExtra("DrawToRunData",
                            DrawToRunData(
                                drawToRunData.touchList,
                                drawToRunData.startLatLng,
                                drawToRunData.totalDistance,
                                drawToRunData.departure,
                                drawToRunData.captureUri))
                    }
                    startActivity(intent)
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.isCount.startAnimation(anim)


    }

}