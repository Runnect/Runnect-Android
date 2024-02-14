package com.runnect.runnect.util.analytics

object EventName {

    // App
    const val EVENT_VIEW_HOME = "view_home" // 앱 실행
    const val EVENT_VIEW_SOCIAL_LOGIN = "view_social_login"

    // Login
    const val EVENT_CLICK_GOOGLE_LOGIN = "click_google_login"
    const val EVENT_CLICK_KAKAO_LOGIN = "click_kakao_login"
    const val EVENT_CLICK_VISITOR = "click_visitor"

    // Running Tracking
    const val EVENT_CLICK_BACK_RUNNING_TRACKING = "click_back_running_tracking"
    const val EVENT_CLICK_STORE_RUNNING_TRACKING = "click_store_running_tracking"

    // Draw Course
    const val EVENT_CLICK_COURSE_DRAWING = "click_course_drawing"
    const val EVENT_CLICK_CURRENT_LOCATE = "click_current_locate"
    const val EVENT_CLICK_MAP_LOCATE = "click_map_locate"
    const val EVENT_CLICK_STORED_AFTER_COURSE_COMPLETE = "click_stored_after_course_complete"
    const val EVENT_CLICK_RUN_AFTER_COURSE_COMPLETE = "click_run_after_course_complete"

    // Navigation Menu
    const val EVENT_CLICK_NAV_COURSE_DRAWING = "click_nav_course_drawing"
    const val EVENT_CLICK_NAV_COURSE_DISCOVERY = "click_nav_course_discovery"
    const val EVENT_CLICK_NAV_STORAGE = "click_nav_storage"
    const val EVENT_CLICK_NAV_MY_PAGE = "click_nav_my_page"

    // Visitor Mode
    const val EVENT_CLICK_JOIN_IN_COURSE_DRAWING = "click_join_in_course_drawing"
    const val EVENT_CLICK_JOIN_IN_COURSE_DISCOVERY = "click_join_in_course_discovery"
    const val EVENT_CLICK_JOIN_IN_STORAGE = "click_join_in_storage"
    const val EVENT_CLICK_JOIN_IN_MY_PAGE = "click_join_in_my_page"

    // Discover
    const val EVENT_CLICK_UPLOAD_BUTTON = "click_upload_button"
    const val EVENT_CLICK_DATE = "click_date_sort"
    const val EVENT_CLICK_SCRAP = "click_scrap_sort"
    const val EVENT_CLICK_TRY_BANNER = "click_try_banner"
    const val EVENT_CLICK_TRY_SEARCH_COURSE = "click_try_search_course"
    const val EVENT_CLICK_SHARE = "click_share"
    const val EVENT_CLICK_USER_PROFILE = "click_user_profile"
    const val EVENT_CLICK_COURSE_UPLOAD = "click_course_upload"

    const val VIEW_COURSE_SEARCH = "view_course_search"
    const val VIEW_COURSE_DETAIL = "view_course_detail"
    const val VIEW_USER_PROFILE = "view_user_profile"
    const val VIEW_COURSE_UPLOAD = "view_course_upload"

    // MyPage
    const val EVENT_CLICK_RUNNING_RECORD = "click_running_record"
    const val EVENT_CLICK_GOAL_REWARD = "click_goal_reward"
    const val EVENT_CLICK_UPLOADED_COURSE = "click_uploaded_course"

    // MyHistory
    const val EVENT_CLICK_COURSE_DRAWING_IN_RUNNING_RECORD =
        "click_course_drawing_in_running_record"

    // MySettingAccountInfo
    const val EVENT_VIEW_SUCCESS_LOGOUT = "view_success_logout"
    const val EVENT_CLICK_TRY_LOGOUT = "click_try_logout"
    const val EVENT_VIEW_SUCCESS_WITHDRAW = "view_success_withdraw"
    const val EVENT_CLICK_TRY_WITHDRAW = "click_try_withdraw"

    // MyUpload
    const val EVENT_CLICK_COURSE_UPLOAD_IN_UPLOADED_COURSE =
        "click_course_upload_in_uploaded_course"

    // StorageMain
    const val EVENT_CLICK_MY_DRAW_STORAGE_COURSE_DRAWING_START =
        "click_my_storage_course_drawing_start"
    const val EVENT_CLICK_SCRAP_COURSE = "click_scrap_course"

    // StorageMyDraw
    const val EVENT_MY_STORAGE_TRY_REMOVE = "click_my_storage_try_remove"
}