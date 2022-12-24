package com.example.runnect.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

// Fragment의 기본을 작성, 뷰 바인딩 활용
abstract class BindingFragment<B : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int
) : Fragment() {
    private var _binding: B? = null
    protected val binding
        get() = requireNotNull(_binding!!) { "${this::class.java.simpleName} error" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}