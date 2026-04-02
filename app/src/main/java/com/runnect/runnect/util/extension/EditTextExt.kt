package com.runnect.runnect.util.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/** 키보드 자동 올리기 */
fun EditText.showKeyboard(context: Context) {
    requestFocus()
    setSelection(text.length)
    isCursorVisible = true

    postDelayed({
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, 0)
    }, 100)
}
