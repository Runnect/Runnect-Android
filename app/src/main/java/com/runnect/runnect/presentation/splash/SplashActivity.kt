package com.runnect.runnect.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.runnect.runnect.R
import com.runnect.runnect.presentation.login.LoginActivity
import com.runnect.runnect.presentation.scheme.SchemeActivity


class SplashActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }, DELAY_TIME)
    }

    companion object {
        private const val DELAY_TIME = 1000L
    }
}
