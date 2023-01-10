package com.example.runnect.presentation.prestart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.example.runnect.R
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.InfoWindow.DefaultViewAdapter

open class infoAdapter(context: Context, parent : ViewGroup) : DefaultViewAdapter(context) {

        val mContext = context
        val mParent = parent

    override fun getContentView(infoWindow: InfoWindow): View {
        val view : View = LayoutInflater.from(mContext).inflate(R.layout.item_info, mParent, false)
//        view.setBackgroundColor(context.resources.getColor(R.color.M1))
//        view.setBackgroundResource(context.resources.getDrawable())

      return view
    }
}