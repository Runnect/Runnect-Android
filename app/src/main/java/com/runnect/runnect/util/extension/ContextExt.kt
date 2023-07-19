package com.runnect.runnect.util.extension

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.runnect.runnect.R
import kotlinx.android.synthetic.main.custom_dialog_delete.*
import kotlinx.android.synthetic.main.custom_dialog_delete.view.*
import kotlinx.android.synthetic.main.custom_dialog_edit_mode.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet.btn_delete_yes


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
}

fun Context.setAlertDialog(
    layoutInflater: LayoutInflater,
    view: View,
    resId: Int,
): Pair<AlertDialog, View> {
    val dialogLayout = layoutInflater.inflate(resId, null)
    val build = AlertDialog.Builder(view.context).apply {
        setView(dialogLayout)
    }
    val dialog = build.create()
    dialog.setCancelable(false) // 외부 영역 터치 금지
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return Pair(dialog, dialogLayout)
}



fun Context.setCustomDialog(
    layoutInflater: LayoutInflater,
    view: View,
    description: String,
    yesBtnText: String,
    noBtnText: String = "취소"
): AlertDialog {
    val dialogLayout = layoutInflater.inflate(R.layout.custom_dialog_delete, null)
    with(dialogLayout) {
        tv_dialog.text = description
        btn_delete_no.text = noBtnText
        btn_delete_yes.text = yesBtnText
    }
    val build = AlertDialog.Builder(view.context).apply {
        setView(dialogLayout)
    }
    val dialog = build.create()
    dialog.setCancelable(false) //외부 영역 터치 금지
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialog
}

fun AlertDialog.setDialogClickListener(listener: (which: AppCompatButton) -> Unit) {
    this.setOnShowListener {
        val yesButton = this.btn_delete_yes
        val noButton = this.btn_delete_no
        yesButton.setOnClickListener {
            listener(yesButton)
            this.dismiss()
        }
        noButton.setOnClickListener {
            listener(noButton)
            this.dismiss()
        }
    }
}

fun Context.setEditBottomSheet(): BottomSheetDialog {
    val bottomSheetDialog = BottomSheetDialog(
        this, R.style.BottomSheetDialogTheme
    )
    val bottomSheetView = LayoutInflater.from(this).inflate(
        R.layout.custom_dialog_edit_mode, null
    )
    bottomSheetDialog.setContentView(bottomSheetView)
    return bottomSheetDialog
}

fun BottomSheetDialog.setEditBottomSheetClickListener(listener: (which: LinearLayoutCompat) -> Unit) {
    this.setOnShowListener {
        val editButton = this.layout_edit_frame
        val deleteButton = this.layout_delete_frame
        editButton.setOnClickListener {
            listener(editButton)
            dismiss()
        }
        deleteButton.setOnClickListener {
            listener(deleteButton)
            dismiss()
        }
    }
}


fun Context.getStampResId(
    stampId: String?,
    resNameParam: String,
    resType: String,
    packageName: String
): Int {
    with(this) {
        var resName = ""
        if(stampId == "CSPR0"){
            resName = "${resNameParam}basic"
            return resources.getIdentifier(
                resName,
                resType, packageName
            )
        } else {
            resName = "${resNameParam}$stampId"
            return resources.getIdentifier(
                resName,
                resType, packageName
            )
        }
    }
}

fun Context.startWebView(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}