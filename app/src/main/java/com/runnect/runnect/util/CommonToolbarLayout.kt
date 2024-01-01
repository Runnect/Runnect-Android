package com.runnect.runnect.util

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.runnect.runnect.R
import com.runnect.runnect.databinding.LayoutCommonToolbarBinding
import com.runnect.runnect.util.extension.dpToPx

interface CommonToolbarLayout {

    @IntDef(LEFT, RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Direction

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
        const val MENU_ITEM_SIZE = 48 // 메뉴 아이템 뷰 기본 크기
        const val MENU_ITEM_PADDING = 15 // 메뉴 아이템 뷰 기본 패딩(dp)
        const val MENU_ITEM_TEXT_SIZE = 18 // 텍스트 기본 크기
        const val MENU_ITEM_RESOURCE_NONE = -1 // 메뉴 아이템 리소스(아이콘,텍스트) 없음

        private const val TOOLBAR_HEIGHT = 56 // Toolbar 기본 높이
        private const val TOOLBAR_TITLE_TEXT_SIZE = 18 // Title 텍스트 기본 크기(sp)
        private const val MENU_LIMIT_COUNT = 2 // 메뉴 최대 노출 갯수
        private const val VIEW_SIZE_NONE = -1 // 뷰 사이즈 없음

        /** 리소스 관련 상수 */
        // 메뉴 뒤로가기 기본 아이콘
        private const val MENU_ICON_BACK = R.drawable.all_back_arrow
        // Title 텍스트 기본 색상
        private const val TOOLBAR_TITLE_TEXT_COLOR = R.color.G1
        // Toolbar 기본 배경 색상
        private const val TOOLBAR_BACKGROUND_COLOR = R.color.white
    }

    val toolbarBinding: LayoutCommonToolbarBinding

    /**
     * ToolBar 초기화 메소드
     */
    fun initToolBarLayout()

    /**
     * 툴바의 기본 형태를 설정
     *
     * @param bgColorResId 툴바의 배경색 리소스 id
     * @param textResId title 텍스트 리소스 id
     * @param textSizeDip title 텍스트의 크기(sp)
     * @param textColorResId title 텍스트 색상의 리소스 id
     * @param backButtonResId back 버튼의 아이콘 리소스 id
     * @param backButtonEvent back 버튼의 클릭 이벤트 핸들러
     */
    fun setToolbar (
        @ColorRes bgColorResId: Int = TOOLBAR_BACKGROUND_COLOR,
        @StringRes textResId: Int? = null,
        titleText: String? = null,
        textSizeDip: Int = TOOLBAR_TITLE_TEXT_SIZE,
        @ColorRes textColorResId: Int = TOOLBAR_TITLE_TEXT_COLOR,
        @DrawableRes backButtonResId: Int = MENU_ICON_BACK,
        backButtonEvent: ((View) -> Unit)? = null
    ) {
        // 백그라운드 영역
        setToolbarBackgroundColor(bgColorResId)

        // title 영역 text
        if(textResId != null) {
            setAppBarTitleText(textResId)
        } else if (titleText != null) {
            setAppBarTitleText(titleText)
        }

        setAppBarTitleTextColor(textColorResId)
        setAppBarTitleTextSize(textSizeDip)

        // 왼쪽 메뉴 추가
        addMenuTo(
            LEFT,
            ToolbarMenu.Icon(
                resourceId = backButtonResId,
                clickEvent = backButtonEvent,
            )
        )
    }

    /**
     * Toolbar의 배경 색상 지정 메소드
     * @param colorResId 배경 색상 Color Resource Id
     */
    fun setToolbarBackgroundColor(@ColorRes colorResId: Int = TOOLBAR_BACKGROUND_COLOR) {
        toolbarBinding.toolbar.run {
            context?.let {
                setBackgroundColor(getColor(it, colorResId))
            }
        }
    }

    /**
     * AppBar의 Title Text 설정
     * @param textResId title 텍스트 리소스 id
     */
    fun setAppBarTitleText(@StringRes textResId: Int) {
        toolbarBinding.tvTitle.run {
            text = context?.getText(textResId)
        }
    }

    /**
     * AppBar의 Title Text 설정
     * @param title title 텍스트
     */
    fun setAppBarTitleText(title: String) {
        toolbarBinding.tvTitle.text = title
    }

    /**
     * Toolbar의 Title Text Color 설정
     * @param textColorResId 색상 리소스 id
     */
    fun setAppBarTitleTextColor(@ColorRes textColorResId: Int = TOOLBAR_TITLE_TEXT_COLOR) {
        toolbarBinding.tvTitle.run {
            context?.let {
                setTextColor(getColor(it, textColorResId))
            }
        }
    }

    /**
     * Toolbar의 Title Text Size 설정
     * @param textSize 텍스트 사이즈(sp)
     */
    fun setAppBarTitleTextSize(textSize: Int = TOOLBAR_TITLE_TEXT_SIZE) {
        toolbarBinding.tvTitle.run {
            context?.let {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
            }
        }
    }

    /**
     * Toolbar 높이 설정 메소드
     * @param height 높이 값(dp)
     */
    fun setAppBarHeight(height: Int = TOOLBAR_HEIGHT) {
        toolbarBinding.toolbar.run {
            context?.let {
                setViewSize(toolbarBinding.toolbar, height = height.dpToPx(it))
            }
        }
    }

    /**
     * Toolbar의 왼쪽 영역에 메뉴 아이템들을 추가합니다.
     *
     * @param direction 메뉴가 추가될 위치 (Toolbar의 왼쪽, 오른쪽)
     * @param menu 추가할 메뉴 아이템 리스트(가변 인자)
     */
    fun addMenuTo(@Direction direction: Int, vararg menu: ToolbarMenu) {
        val menuLayout = when(direction) {
            LEFT -> toolbarBinding.llLeftMenu
            else -> toolbarBinding.llRightMenu
        }
        val context = menuLayout.context ?: return

        menu.forEach { item ->
            if (canAddMenu(menuLayout)) {
                addMenuView(context, menuLayout, item)
            }
        }
    }

    /**
     * 뷰 size 설정
     * @param view
     * @param width
     * @param height
     */
    private fun setViewSize(view: View, width: Int = VIEW_SIZE_NONE, height: Int = VIEW_SIZE_NONE) {
        view.layoutParams = view.layoutParams.apply {
            this.width = if (width > VIEW_SIZE_NONE) width else this.width
            this.height = if (height > VIEW_SIZE_NONE) height else this.height
        }
    }

    /**
     * Toolbar에 메뉴 추가
     *
     * @param parent 메뉴가 추가될 부모 뷰
     * @param toolbarMenu 추가할 메뉴
     */
    private fun addMenuView(context: Context?, parent: LinearLayout, toolbarMenu: ToolbarMenu) {
        context ?: return
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val menuView: View = when (toolbarMenu) {
            is ToolbarMenu.Icon -> AppCompatImageButton(context).apply {
                with(toolbarMenu) {
                    // 이미지뷰 패딩 설정 (기본 15dp)
                    setPadding(padding.dpToPx(context))
                    setBackgroundColor(
                        getColor(context, R.color.transparent_00)
                    )
                    // 이미지 아이콘 설정
                    setImageResource(resourceId)
                    // 클릭 이벤트 설정
                    setOnClickListener {
                        clickEvent?.invoke(this@apply)
                    }
                    // 이미지뷰 사이즈 (기본 48dp * 48dp)
                    setLayoutParams(
                        layoutParams.apply {
                            width = toolbarMenu.width.dpToPx(context)
                            height = toolbarMenu.height.dpToPx(context)
                        }
                    )
                }
            }

            is ToolbarMenu.TextStyle -> TextView(context).apply {
                with(toolbarMenu) {
                    // 텍스트뷰 패딩 설정 기본(0dp)
                    val padding = padding.dpToPx(context)
                    setPadding(padding, 0, padding, 0)

                    text = context.getString(resourceId)
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)

                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
                    setTextColor(
                        getColor(context, TOOLBAR_TITLE_TEXT_COLOR)
                    )

                    setLayoutParams(layoutParams)
                }
            }

            is ToolbarMenu.Popup -> AppCompatImageButton(context).apply {

            }
        }

        parent.addView(menuView)
    }

    private fun canAddMenu(parent: LinearLayout): Boolean = parent.childCount < MENU_LIMIT_COUNT

    /* 유틸 메소드 */
    private fun View.setPadding(@Px size: Int) {
        setPadding(size, size, size, size)
    }

    private fun getColor(context: Context, @ColorRes colorResId: Int): Int {
        return ContextCompat.getColor(context, colorResId)
    }
}