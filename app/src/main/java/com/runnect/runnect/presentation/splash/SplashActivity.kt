package com.runnect.runnect.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.runnect.runnect.R
import com.runnect.runnect.presentation.MainActivity


class SplashActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        moveMain(1)
    }
    private fun moveMain(sec: Int) {
        handler.postDelayed(Runnable {
            //new Intent(현재 context, 이동할 activity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) //intent 에 명시된 액티비티로 이동
            finish() //현재 액티비티 종료
        }, (1000 * sec).toLong()) // sec초 정도 딜레이를 준 후 시작
    }
}