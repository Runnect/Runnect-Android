package com.runnect.runnect.util.custom.toolbar

import android.view.View

sealed class ToolbarMenu(
    open val resourceId: Int,
    open val padding: Int,
    open val clickEvent: ((View) -> Unit)?
) {

    data class Icon (
        override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = CommonToolbarLayout.MENU_ITEM_PADDING,
        override val clickEvent: ((View) -> Unit)? = null,
        val width: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val height: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
    ) : ToolbarMenu(resourceId, padding, clickEvent)

    data class TextStyle(
        override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = 0,
        override val clickEvent: ((View) -> Unit)? = null,
        val textSize: Int = CommonToolbarLayout.MENU_ITEM_TEXT_SIZE,
    ) : ToolbarMenu(resourceId, padding, clickEvent)

    data class Popup (
        override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = CommonToolbarLayout.MENU_ITEM_PADDING,
        override val clickEvent: ((View) -> Unit)? = null,
        val width: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val height: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
    ) : ToolbarMenu(resourceId, padding, clickEvent)
}