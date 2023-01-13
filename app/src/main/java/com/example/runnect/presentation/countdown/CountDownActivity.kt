package com.example.runnect.presentation.countdown

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.data.model.entity.LocationLatLngEntity
import com.example.runnect.databinding.ActivityCountDownBinding
import com.example.runnect.presentation.run.RunActivity
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CountDownActivity : BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {

    private var touchList = arrayListOf<LatLng>()
    lateinit var startLatLngPublic: LocationLatLngEntity


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
                    Log.d("counter", counter.toString())
                    binding.isCount.setImageDrawable(numList[counter])
                    binding.isCount.startAnimation(animation)
                }


                if (counter == 2) {
                    touchList = intent.getSerializableExtra("touchList") as ArrayList<LatLng>
                    startLatLngPublic = intent.getParcelableExtra("startLatLng")!!
                    val totalDistance = intent.getSerializableExtra("totalDistance")

                    Timber.tag(ContentValues.TAG).d("startLatLng : ${startLatLngPublic}")
                    Timber.tag(ContentValues.TAG).d("touchList : ${touchList}")
                    Timber.tag(ContentValues.TAG).d("totalDistance : ${totalDistance}") //수신 완료

                    val intent = Intent(this@CountDownActivity, RunActivity::class.java).apply {
                        putExtra("touchList", touchList)
                        putExtra("startLatLng", startLatLngPublic)
                        putExtra("totalDistance", totalDistance)
                    }
                    startActivity(intent)
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.isCount.startAnimation(anim)


    }

}