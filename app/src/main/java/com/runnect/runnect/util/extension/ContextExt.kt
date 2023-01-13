package com.runnect.runnect.util.extension

import android.content.Context

import android.view.View
import android.view.inputmethod.InputMethodManager

import android.widget.Toast

//확장할 클래스 - Context 클래스
//Context 클래스에 속한 인스턴스 객체 - this
//함수 내부에서는 this 키워드로 수신 객체 멤버 사용

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

//키보드 내리기(포커스 해제)
fun Context.clearFocus(view: View) {
    val imm: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
