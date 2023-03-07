package com.runnect.runnect.presentation.countdown

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import com.runnect.runnect.R
import com.runnect.runnect.data.model.DetailToRunData
import com.runnect.runnect.data.model.DrawToRunData
import com.runnect.runnect.data.model.MyDrawToRunData
import com.runnect.runnect.databinding.ActivityCountDownBinding
import com.runnect.runnect.presentation.run.RunActivity

class CountDownActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {

    val drawToRunData: DrawToRunData? = intent.getParcelableExtra("DrawToRunData")
    //drawToRunData만 좌표가 StartLatLng이랑 touchList로 나눠져있고 나머지 2개 data class는 똑같음.
    //RunActivity보면 좌표를 구분짓는 게 맞는데 아래 2개는 안 돼있어서 for문으로 touchList를 새로 만들어주는 작업이 있음.
    //data class를 통일하면 편하니까 detail이랑 myDraw 쪽에서 미리 StartLatLng이랑 touchList를 구분지어서 보내주면 될 것 같은데?
    //이러면 RunActivity에서 크게 손 안 대도 됨.
    val detailToRunData: DetailToRunData? = intent.getParcelableExtra("detailToRun")
    val myDrawToRunData: MyDrawToRunData? = intent.getParcelableExtra("myDrawToRun")

    //drawToRunData, detailToRunData, myDrawToRunData 3개 data class 내부 구성 똑같이 통일하고
    //이 3개에 대한 getExtra를 한 번에 실행한다음 not null인 것만 CountDownToRunData에 담아서 intent로 putExtra
    //고민인 건, 굳이 CountDownToRunData를 만들어줘야 하냐는 것

    //myDrawToRunData는 startLatLng이랑 touchList를 미리 쪼개서 보내도록 수정해줬는데
    //detailToRunData는 서버에서 path까지 받아올 수 있게 api를 수정해주고난 다음에야 작업 가능.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private fun setAnimationListener(
        anim: Animation,
        numList: ArrayList<Drawable?>,
        intentToRun: Intent
    ) {
        var counter = -1

        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                counter += 1
                if (counter == 2) {
                    intentToRun.apply {
                        //여기에 CountDownToRunData 만들어서 putExtra 해야 함
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