package com.runnect.runnect.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.runnect.runnect.R
import com.runnect.runnect.presentation.login.LoginActivity


class SplashActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        moveMain(1)
    }

    private fun moveMain(sec: Int) {
        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
            finish()
        }, (1000 * sec).toLong())
    }
}