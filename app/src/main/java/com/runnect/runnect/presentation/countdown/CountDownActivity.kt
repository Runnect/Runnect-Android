package com.runnect.runnect.presentation.countdown

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import com.runnect.runnect.data.model.entity.LocationLatLngEntity
import com.runnect.runnect.presentation.run.RunActivity
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.data.model.entity.SearchResultEntity
import com.runnect.runnect.databinding.ActivityCountDownBinding
import timber.log.Timber

class CountDownActivity : com.runnect.runnect.binding.BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {

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
                    val totalDistance = intent.getSerializableExtra("totalDistance") //총거리

                    val departure = intent.getStringExtra("departure") //출발지
                    val captureUri = intent.getStringExtra("captureUri") //이미지 url 에러뜨면 String으로,,


                    Timber.tag(ContentValues.TAG).d("startLatLng : ${startLatLngPublic}")
                    Timber.tag(ContentValues.TAG).d("touchList : ${touchList}")
                    Timber.tag(ContentValues.TAG).d("totalDistance : ${totalDistance}")
                    Timber.tag(ContentValues.TAG).d("departure : ${departure}")
                    Timber.tag(ContentValues.TAG).d("captureUri : ${captureUri}")
                    //수신 완료

                    val intent = Intent(this@CountDownActivity, RunActivity::class.java).apply {
                        putExtra("touchList", touchList)
                        putExtra("startLatLng", startLatLngPublic)
                        putExtra("totalDistance", totalDistance)
                        putExtra("departure",departure)
                        putExtra("captureUri",captureUri)
                    }
                    startActivity(intent)
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.isCount.startAnimation(anim)


    }

}