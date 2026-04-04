package com.runnect.runnect.presentation.mypage.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.runnect.runnect.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_setting)

        if (savedInstanceState == null) {
            val bundle = Bundle().apply {
                putString(
                    MySettingFragment.ACCOUNT_INFO_TAG,
                    intent.getStringExtra(MySettingFragment.ACCOUNT_INFO_TAG)
                )
            }
            supportFragmentManager.commit {
                replace<MySettingFragment>(R.id.fl_main, args = bundle)
            }
        }
    }
}
