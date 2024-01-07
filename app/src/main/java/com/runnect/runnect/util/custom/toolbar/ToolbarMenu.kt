package com.runnect.runnect.util.custom.toolbar

import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import com.runnect.runnect.util.custom.popup.PopupItem
import com.runnect.runnect.util.custom.popup.RunnectPopupMenu
import com.runnect.runnect.util.extension.dpToPx

sealed class ToolbarMenu(
    open val resourceId: Int,
    open val padding: Int,
): BaseToolbarMenu() {

    abstract fun createMenu(
        context: Context
    ): View

    data class Icon (
        @DrawableRes override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = CommonToolbarLayout.MENU_ITEM_PADDING,
        val width: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val height: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val clickEvent: ((View) -> Unit)? = null,
    ): ToolbarMenu(resourceId, padding) {

        override fun createMenu(
            context: Context,
        ): View {
            return createBaseImageButton(context, resourceId, padding, width, height, clickEvent)
        }
    }

    data class Text (
        @StringRes override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = 0,
        val textSize: Int = CommonToolbarLayout.MENU_ITEM_TEXT_SIZE,
        @FontRes val fontRes: Int = CommonToolbarLayout.TOOLBAR_TITLE_FONT_RES
    ): ToolbarMenu(resourceId, padding) {

        override fun createMenu(
            context: Context
        ): View {
            return createBaseTextView(context, resourceId, padding, textSize.toFloat(), fontRes)
        }
    }

    data class Popup (
        @DrawableRes override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = CommonToolbarLayout.MENU_ITEM_PADDING,
        val width: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val height: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        private val popupItems: List<PopupItem>,
        private val menuItemClickListener: (View, PopupItem, Int) -> Unit,
    ): ToolbarMenu(resourceId, padding) {

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