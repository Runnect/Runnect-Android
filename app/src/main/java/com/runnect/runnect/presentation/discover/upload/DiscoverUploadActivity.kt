package com.runnect.runnect.presentation.discover.upload

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.databinding.ActivityDiscoverUploadBinding
import com.runnect.runnect.presentation.MainActivity
import timber.log.Timber


class DiscoverUploadActivity :
    BindingActivity<ActivityDiscoverUploadBinding>(R.layout.activity_discover_upload) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        addListener()

    }

    private fun initLayout() {
        binding.etDiscoverUploadDesc.movementMethod = null //내용 초과시 스크롤 되지 않도록 함
    }
    private fun addListener() {
        binding.ivDiscoverUploadBack.setOnClickListener {
            finish()
        }
        binding.ivDiscoverUploadFinish.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        //키보드 이벤트에 따른 동작 정의
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            val heightDiff: Int = binding.root.rootView.height - (r.bottom - r.top)
            if (heightDiff > 200) { //root view의 높이가 200이상일 때에는 keyboard up
                //ok now we know the keyboard is up...
                binding.ivDiscoverUploadFinish.visibility = View.GONE
                binding.tvDiscoverUploadFinish.visibility = View.GONE
            } else {
                //그 이하일 때에는 keyboard down
                binding.ivDiscoverUploadFinish.visibility = View.VISIBLE
                binding.tvDiscoverUploadFinish.visibility = View.VISIBLE
            }
        }
    }

    //키보드 밖 터치 시, 키보드 내림
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if(focusView != null){
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev!!.x.toInt()
            val y = ev.y.toInt()
            if(!rect.contains(x,y)){
                val imm :InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken,0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}