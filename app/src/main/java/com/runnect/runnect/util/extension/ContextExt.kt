package com.runnect.runnect.util.extension

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.SlideDistanceProvider.GravityFlag
import com.runnect.runnect.R
import com.runnect.runnect.databinding.CustomDialogDeleteBinding
import timber.log.Timber

fun Context.setActivityDialog(
    layoutInflater: LayoutInflater,
    binding: ViewBinding,
    cancel: Boolean
): Pair<AlertDialog, View> {
    val build = AlertDialog.Builder(this).apply {
        setView(binding.root)
    }
    val dialog = build.create()
    dialog.setCancelable(cancel) // 외부 영역 터치 금지
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return Pair(dialog, binding.root)
}

fun Fragment.setFragmentDialog(
    resId: Int,
    cancel: Boolean
): Pair<AlertDialog, View> {
    val dialogLayout = layoutInflater.inflate(resId, null)
    val build = AlertDialog.Builder(requireContext()).apply {
        setView(dialogLayout)
    }
    val dialog = build.create()
    dialog.setCancelable(cancel) // 외부 영역 터치 금지
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return Pair(dialog, dialogLayout)
}


fun Context.setCustomDialog(
    binding: CustomDialogDeleteBinding,
    description: String,
    yesBtnText: String,
    noBtnText: String = "취소"
): AlertDialog {
    binding.tvDialog.text = description
    binding.btnDeleteNo.text = noBtnText
    binding.btnDeleteYes.text = yesBtnText

    val build = AlertDialog.Builder(this).apply {
        setView(binding.root)
    }
    val dialog = build.create()
    dialog.setCancelable(false) //외부 영역 터치 금지
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialog
}

fun AlertDialog.setDialogButtonClickListener(
    binding: CustomDialogDeleteBinding,
    listener: (which: AppCompatButton) -> Unit
) {
    this.setOnShowListener {
        binding.btnDeleteYes.setOnClickListener {
            listener(binding.btnDeleteYes)
            this.dismiss()
        }
        binding.btnDeleteNo.setOnClickListener {
            listener(binding.btnDeleteNo)
            this.dismiss()
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
        if (stampId == "CSPR0") {
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

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}

fun Context.showWebBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showSnackbar(
    anchorView: View,
    message: String,
    @GravityFlag gravity: Int = Gravity.BOTTOM
) {
    val snackbar = Snackbar.make(anchorView, message, Snackbar.LENGTH_SHORT)
    val layoutParams = snackbar.view.layoutParams as? CoordinatorLayout.LayoutParams
    layoutParams?.apply {
        this.gravity = gravity
        snackbar.view.layoutParams = this
    }
    snackbar.show()
}

fun Context.colorOf(@ColorRes resId: Int) = ContextCompat.getColor(this, resId)

fun Context.drawableOf(@DrawableRes resId: Int) = ContextCompat.getDrawable(this, resId)

fun Context.fontOf(@FontRes resId: Int, @StyleRes style: Int): Typeface =
    Typeface.create(ResourcesCompat.getFont(this, resId), style)

/**
 * Status Bar 색상을 변경
 * isLightColor : 변경할 status bar 색상이 밝은 색인지 (시계, 노티 아이콘 등 색상을 어둡게 변경하기 위해서)
 * colorResource : 변경할 색상 리소스
 */
fun Context.setStatusBarColor(window: Window?, isLightColor: Boolean, colorResource: Int) {
    runCatching {
        window?.apply {
            // 조건에 따른 시스템 UI 가시성 설정
            if (isLightColor) {
                val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                } else {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    insetsController?.setSystemBarsAppearance(flag, flag)
                } else {
                    decorView.systemUiVisibility = flag
                }
            }

            // Status bar 색상 설정
            statusBarColor = ContextCompat.getColor(context, colorResource)
        }
    }.onFailure { e ->
        Timber.e("Failed to set status bar color: ${e.message}")
    }
}