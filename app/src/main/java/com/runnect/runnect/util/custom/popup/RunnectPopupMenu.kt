package com.runnect.runnect.util.custom.popup

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.runnect.runnect.R
import com.runnect.runnect.databinding.ItemRunnectPopupMenuBinding
import com.runnect.runnect.databinding.LayoutRunnectPopupMenuBinding
import com.runnect.runnect.util.extension.drawableOf

class RunnectPopupMenu(
    private val context: Context,
    private val popupItems: List<PopupItem>,
    private val menuItemClickListener: (View, PopupItem, Int) -> Unit
) : PopupWindow(context) {
    init {
        isOutsideTouchable = true
        isTouchable = true
        inflateMenuItemsToLayout()
        setupContentView()
    }

    private fun inflateMenuItemsToLayout() {
        val inflater = LayoutInflater.from(context)
        val layoutBinding = LayoutRunnectPopupMenuBinding.inflate(inflater, null, false)
        contentView = layoutBinding.root

        for (i in popupItems.indices) {
            val itemBinding = ItemRunnectPopupMenuBinding.inflate(inflater, null, false)
            itemBinding.apply {
                ivPopupIcon.setImageResource(popupItems[i].resId)
                tvPopupTitle.text = popupItems[i].title
                vPopupDivider.visibility =
                    if (i < popupItems.size - 1) View.VISIBLE else View.INVISIBLE
            }

            val menuItemView = itemBinding.root
            menuItemView.setOnClickListener { view ->
                menuItemClickListener.invoke(view, popupItems[i], i)
                dismiss()
            }
            layoutBinding.llPopupMenu.addView(menuItemView)
        }
    }

    private fun setupContentView() {
        width = getDp(context, POPUP_MENU_WIDTH)
        contentView.measure(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        height = contentView.measuredHeight

        setBackgroundDrawable(context.drawableOf(R.drawable.shape_transparent_10_rect))
    }

    private fun getDp(context: Context, value: Float): Int {
        val dm = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm).toInt()
    }

    companion object {
        private const val POPUP_MENU_WIDTH = 170F
    }
}