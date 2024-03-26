package com.runnect.runnect.util.custom.toolbar

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.runnect.runnect.R
import com.runnect.runnect.databinding.LayoutCommonToolbarBinding
import timber.log.Timber

interface CommonToolbarLayout {

    @IntDef(LEFT, RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Direction

    val toolbarBinding: LayoutCommonToolbarBinding

    /**
     * ToolBar 초기화 메소드
     * Activity or Fragment에서 구현
     */
    fun initToolBarLayout()

    /**
     * 툴바의 기본 형태를 설정
     *
     * @param bgColorResId 툴바의 배경색 리소스 id
     * @param textResId title 텍스트 리소스 id
     * @param textSize title 텍스트의 크기(sp)
     * @param textColorResId title 텍스트 색상의 리소스 id
     * @param fontResId title 폰트 리소스 id
     * @param backButtonResId back 버튼의 아이콘 리소스 id
     * @param backButtonEvent back 버튼의 클릭 이벤트 핸들러
     */
    fun setToolbar(
        @ColorRes bgColorResId: Int = TOOLBAR_BACKGROUND_COLOR,
        @StringRes textResId: Int? = null,
        @ColorRes textColorResId: Int = TOOLBAR_TITLE_TEXT_COLOR,
        @FontRes fontResId: Int = TOOLBAR_TITLE_FONT_RES,
        @DrawableRes backButtonResId: Int = MENU_ICON_BACK_BUTTON,
        titleText: String? = null,
        textSize: Int = TOOLBAR_TITLE_TEXT_SIZE,
        backButtonEvent: ((View) -> Unit)? = null
    ) {
        // title 영역 text
        if (textResId != null) {
            setToolBarTitleText(textResId)
        } else if (titleText != null) {
            setToolBarTitleText(titleText)
        }

        setToolbarBackgroundColor(bgColorResId)
        setToolBarTitleTextColor(textColorResId)
        setToolbarTitleTextSize(textSize)
        setToolBarTitleFont(fontResId)

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
     * Toolbar의 Title Text 설정
     * - Title Text는 Toolbar의 정가운데에 위치하는 TextView를 의미
     * @param textResId title 텍스트 리소스 id
     */
    fun setToolBarTitleText(@StringRes textResId: Int) {
        toolbarBinding.tvTitle.run {
            text = context?.getText(textResId)
        }
    }

    /**
     * Toolbar의 Title Text 설정
     * - Title Text는 Toolbar의 정가운데에 위치하는 TextView를 의미
     * @param title title 텍스트
     */
    fun setToolBarTitleText(title: String) {
        toolbarBinding.tvTitle.text = title
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
     * Toolbar의 Title Text Color 설정
     * @param textColorResId 색상 리소스 id
     */
    fun setToolBarTitleTextColor(@ColorRes textColorResId: Int = TOOLBAR_TITLE_TEXT_COLOR) {
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
    fun setToolbarTitleTextSize(textSize: Int = TOOLBAR_TITLE_TEXT_SIZE) {
        toolbarBinding.tvTitle.run {
            context?.let {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
            }
        }
    }

    /**
     * Toolbar Title Font 설정
     *
     * @param fontResId - title 폰트 리소스 id
     */
    fun setToolBarTitleFont(@FontRes fontResId: Int) {
        with(toolbarBinding) {
            val context = toolbar.context ?: return
            tvTitle.typeface = ResourcesCompat.getFont(context, fontResId)
        }
    }

    /**
     * Toolbar의 왼쪽 영역에 메뉴 아이템들을 추가합니다.
     *
     * @param direction 메뉴가 추가될 위치 (Toolbar의 왼쪽, 오른쪽)
     * @param menu 추가할 메뉴 아이템 리스트(가변 인자)
     */
    fun addMenuTo(@Direction direction: Int, vararg menu: ToolbarMenu) {
        val menuLayout = when (direction) {
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
     * Toolbar에 메뉴 추가
     *
     * @param parent 메뉴가 추가될 부모 뷰
     * @param toolbarMenu 추가할 메뉴
     */
    private fun addMenuView(context: Context?, parent: LinearLayout, toolbarMenu: ToolbarMenu) {
        context ?: return
        val menuView: View = toolbarMenu.createMenu(context)
        parent.addView(menuView)
    }

    private fun canAddMenu(parent: LinearLayout): Boolean = parent.childCount < TOOLBAR_MENU_LIMIT_COUNT

    /* 유틸 메소드 */
    private fun getColor(context: Context, @ColorRes colorResId: Int): Int {
        return ContextCompat.getColor(context, colorResId)
    }

    companion object {
        // 메뉴 추가 위치
        const val LEFT = 0
        const val RIGHT = 1

        // Toolbar 관련
        private const val TOOLBAR_TITLE_TEXT_SIZE = 18  // Title 텍스트 기본 크기(sp)
        private const val TOOLBAR_MENU_LIMIT_COUNT = 2  // 메뉴 최대 노출 갯수


        // 메뉴 아이템 관련
        const val MENU_ITEM_SIZE = 48           // 메뉴 아이템 뷰 기본 크기
        const val MENU_ITEM_PADDING = 15        // 메뉴 아이템 뷰 기본 패딩(dp)
        const val MENU_ITEM_TEXT_SIZE = 18      // 텍스트 기본 크기
        const val MENU_ITEM_RESOURCE_NONE = -1  // 메뉴 아이템 리소스(아이콘,텍스트) 없음

        // 기본 리소스
        @DrawableRes val MENU_ICON_BACK_BUTTON: Int = R.drawable.all_back_arrow
        @FontRes val TOOLBAR_TITLE_FONT_RES: Int = R.font.pretendard_bold
        @ColorRes private val TOOLBAR_TITLE_TEXT_COLOR = R.color.G1
        @ColorRes private val TOOLBAR_BACKGROUND_COLOR = R.color.white
    }
}