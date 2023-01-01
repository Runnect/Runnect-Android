package com.runnect.runnect.util.extension

import android.content.Context
import android.widget.Toast

//확장할 클래스 - Context 클래스
//Context 클래스에 속한 인스턴스 객체 - this
//함수 내부에서는 this 키워드로 수신 객체 멤버 사용

fun Context.showToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}