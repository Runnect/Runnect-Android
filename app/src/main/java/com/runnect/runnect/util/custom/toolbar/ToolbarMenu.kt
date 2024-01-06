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

sealed class ToolbarMenu(
    open val resourceId: Int,
    open val padding: Int,
    open val clickEvent: ((View) -> Unit)?
) {
    abstract fun createMenu(
        context: Context,
        layoutParams: LinearLayout.LayoutParams,
        menu: ToolbarMenu
    ): View?

    data class Icon (
        override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = CommonToolbarLayout.MENU_ITEM_PADDING,
        override val clickEvent: ((View) -> Unit)? = null,
        val width: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val height: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
    ) : ToolbarMenu(resourceId, padding, clickEvent) {
        override fun createMenu(
            context: Context,
            layoutParams: LinearLayout.LayoutParams,
            menu: ToolbarMenu
        ): View? {
            if (menu !is Icon) return null

            val padding = menu.padding.dpToPx(context)
            val menuWidth = menu.width.dpToPx(context)
            val menuHeight = menu.height.dpToPx(context)
            val imgRes = menu.resourceId
            val bgColor = ContextCompat.getColor(context, R.color.transparent_00)
            val clickListener = View.OnClickListener {
                menu.clickEvent?.invoke(it)
            }

            return AppCompatImageButton(context).apply {
                // 이미지 아이콘 설정
                setImageResource(imgRes)
                setBackgroundColor(bgColor)
                setPadding(padding)

                // 클릭 이벤트 설정
                setOnClickListener(clickListener)

                // 이미지뷰 사이즈 설정 (기본 48dp * 48dp)
                layoutParams.apply {
                    width = menuWidth
                    height = menuHeight
                }.let(::setLayoutParams)
            }
        }
    }

    data class Text (
        override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = 0,
        override val clickEvent: ((View) -> Unit)? = null,
        val textSize: Int = CommonToolbarLayout.MENU_ITEM_TEXT_SIZE,
    ) : ToolbarMenu(resourceId, padding, clickEvent) {
        override fun createMenu(
            context: Context,
            layoutParams: LinearLayout.LayoutParams,
            menu: ToolbarMenu
        ): View? {
            if (menu !is Text) return null

            val padding = menu.padding.dpToPx(context)
            val titleText = context.getString(menu.resourceId)
            val textSize = menu.textSize.toFloat()
            val textColor = ContextCompat.getColor(context, R.color.G1)

            return AppCompatTextView(context).apply {
                text = titleText
                gravity = Gravity.CENTER
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)

                // 텍스트뷰 패딩 설정 기본(0dp)
                setPadding(padding, 0, padding, 0)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize)
                setTextColor(textColor)

                setLayoutParams(layoutParams)
            }
        }
    }

    data class Popup (
        override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = CommonToolbarLayout.MENU_ITEM_PADDING,
        override val clickEvent: ((View) -> Unit)? = null,
        val width: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val height: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        private val popupItems: List<PopupItem>,
        private val menuItemClickListener: (View, PopupItem, Int) -> Unit,
    ) : ToolbarMenu(resourceId, padding) {

        private var popupMenu: RunnectPopupMenu? = null

        override fun createMenu(
            context: Context
        ): View {
            return createBaseImageButton(context, resourceId, padding.dpToPx(context), width, height) {
                attachPopupItem(it)
            }
        }

        private fun attachPopupItem(anchorView: View){
            if(popupMenu == null) {
                popupMenu = RunnectPopupMenu(anchorView.context, popupItems) { view, popupItem, pos ->
                    menuItemClickListener.invoke(view, popupItem, pos)
                }
            }

            popupMenu?.showCustomPosition(anchorView)
        }
    }
}