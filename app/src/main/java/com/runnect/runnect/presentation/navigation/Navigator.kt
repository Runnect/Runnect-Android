package com.runnect.runnect.presentation.navigation

import android.content.Context

/** MainActivity가 랜딩 탭을 수신할 때 쓰는 Intent extra 키. Navigator 구현체와 MainActivity만 참조한다. */
const val EXTRA_MAIN_TAB = "extraMainTab"

interface Navigator {
    /**
     * MainActivity로 이동(또는 재사용)하며 랜딩할 탭을 지정한다.
     * @param tab null이면 탭을 지정하지 않는다(기본 그리기 탭 유지).
     */
    fun navigateToMain(
        context: Context,
        tab: MainTab? = null,
        mode: NavigationMode = NavigationMode.DEFAULT
    )
}

enum class MainTab {
    DRAWING, STORAGE, DISCOVER, MY_PAGE
}

/**
 * MainActivity 재진입 시 태스크/백스택 처리 방식.
 * 2단계(NavController 전환)에서는 popUpTo/inclusive 등으로 재해석될 수 있는 "의미"만
 * 표현하고, 실제 Intent 플래그 매핑은 구현체가 담당한다.
 */
enum class NavigationMode {
    /** 플래그 없음. */
    DEFAULT,

    /** FLAG_ACTIVITY_CLEAR_TOP */
    CLEAR_TOP,

    /** FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK */
    NEW_TASK_CLEAR_TASK
}
