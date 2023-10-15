package com.runnect.runnect.binding

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

/** 새로 Activity나 Fragment 만들면 xml이 data binding layout인지 확인할 것 */
abstract class BindingActivity<B : ViewBinding>(@LayoutRes private val layoutRes: Int) :
    AppCompatActivity() {
    lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
    }
}