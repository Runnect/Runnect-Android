package com.runnect.runnect.util

import android.view.View

sealed class ToolbarMenu(
    open val resourceId: Int,
    open val padding: Int,
    open val useRippleEffect: Boolean,
    open val rippleEffect: RippleEffect,
    open val clickEvent: ((View) -> Unit)?
) {

    data class Icon (
        override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = CommonToolbarLayout.MENU_ITEM_PADDING,
        override val clickEvent: ((View) -> Unit)? = null,
        override val useRippleEffect: Boolean = false,
        override val rippleEffect: RippleEffect = RippleEffect.CircleRipple,
        val width: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val height: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
    ) : ToolbarMenu(resourceId, padding, useRippleEffect, rippleEffect, clickEvent)

    data class Popup (
        override val resourceId: Int = CommonToolbarLayout.MENU_ITEM_RESOURCE_NONE,
        override val padding: Int = CommonToolbarLayout.MENU_ITEM_PADDING,
        override val clickEvent: ((View) -> Unit)? = null,
        override val useRippleEffect: Boolean = false,
        override val rippleEffect: RippleEffect = RippleEffect.CircleRipple,
        val width: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
        val height: Int = CommonToolbarLayout.MENU_ITEM_SIZE,
    ) : ToolbarMenu(resourceId, padding, useRippleEffect, rippleEffect, clickEvent)
}