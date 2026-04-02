package com.runnect.runnect.util.extension

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope


fun Fragment.showToast(message:String){
    Toast.makeText(activity,message, Toast.LENGTH_SHORT).show()
}

val Fragment.viewLifeCycle
    get() = viewLifecycleOwner.lifecycle

val Fragment.viewLifeCycleScope
    get() = viewLifecycleOwner.lifecycleScope