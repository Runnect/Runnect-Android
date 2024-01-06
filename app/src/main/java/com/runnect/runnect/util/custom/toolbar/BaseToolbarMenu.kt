package com.runnect.runnect.util.custom.toolbar

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.runnect.runnect.R
import com.runnect.runnect.util.extension.dpToPx
import com.runnect.runnect.util.extension.setPadding

open class BaseToolbarMenu {
    protected fun createBaseImageButton(
        context: Context,
        resourceId: Int,
        padding: Int,
        width: Int,
        height: Int,
        clickEvent: ((View) -> Unit)?
    ): AppCompatImageButton {
        val paddingPx = padding.dpToPx(context)
        val bgColor = ContextCompat.getColor(context, R.color.transparent_00)
        val layoutParams = LinearLayout.LayoutParams(
            width.dpToPx(context),
            height.dpToPx(context)
        )

        return AppCompatImageButton(context).apply {
            setImageResource(resourceId)
            setBackgroundColor(bgColor)
            setPadding(paddingPx)
            setOnClickListener { view -> clickEvent?.invoke(view) }
            setLayoutParams(layoutParams)
        }
    }

    protected fun createBaseTextView(
        context: Context,
        resourceId: Int,
        padding: Int,
        textSize: Float,
    ): AppCompatTextView {
        val paddingPx = padding.dpToPx(context)
        val titleText = context.getString(resourceId)
        val textColor = ContextCompat.getColor(context, R.color.G1)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        return AppCompatTextView(context).apply {
            text = titleText
            gravity = Gravity.CENTER
            typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)

            setPadding(paddingPx, 0, paddingPx, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize)
            setTextColor(textColor)
            setLayoutParams(layoutParams)
        }
    }
}