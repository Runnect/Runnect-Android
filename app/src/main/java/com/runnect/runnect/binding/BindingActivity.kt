package com.runnect.runnect.binding

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import com.runnect.runnect.util.EdgeToEdgeUtil

abstract class BindingActivity<B : ViewBinding>(@LayoutRes private val layoutResId: Int) :
    AppCompatActivity() {
    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        
        // Edge-to-Edge 설정 (모든 BindingActivity에 적용)
        EdgeToEdgeUtil.setupEdgeToEdge(this, binding.root)
    }
}