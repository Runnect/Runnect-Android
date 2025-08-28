package com.runnect.runnect.binding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.runnect.runnect.R
import com.runnect.runnect.presentation.MainActivity
import com.runnect.runnect.presentation.login.LoginActivity

abstract class BaseVisitorFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : BindingFragment<T>(layoutRes) {
    
    abstract val visitorContainer: View
    abstract val contentViews: List<View>
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (MainActivity.isVisitorMode) {
            showVisitorMode()
        } else {
            showContent()
            onContentModeInit()
        }
    }
    
    private fun showVisitorMode() {
        visitorContainer.isVisible = true
        visitorContainer.findViewById<Button>(R.id.btn_visitor_mode)?.setOnClickListener {
            navigateToLogin() 
        }
        contentViews.forEach { it.isVisible = false }
    }
    
    private fun showContent() {
        visitorContainer.isVisible = false
        contentViews.forEach { it.isVisible = true }
    }
    
    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    
    abstract fun onContentModeInit()
}