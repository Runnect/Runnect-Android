package com.example.runnect.presentation.endrun

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.databinding.ActivityEndRunBinding
import kotlin.properties.Delegates

class EndRunActivity : BindingActivity<ActivityEndRunBinding>(R.layout.activity_end_run) {

    //intent로 받아와야 할 거 : 비트맵, 출발지(Run에서 받아오면 될듯), 거리, 타이머
    lateinit var captureBitmap: Bitmap
    var secPublic by Delegates.notNull<Int>()
    var milliPublic by Delegates.notNull<Int>()


    val viewModel: EndRunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backBtn()
        binding.model = viewModel
        binding.lifecycleOwner = this


    }

    fun backBtn() {
        binding.imgBtnBack.setOnClickListener {
            finish()
        }
    }

    //키보드 밖 터치 시, 키보드 내림
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev!!.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                clearFocus(focusView)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //키보드 내리기(포커스 해제) 확장함수
    fun Context.clearFocus(view: View) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}