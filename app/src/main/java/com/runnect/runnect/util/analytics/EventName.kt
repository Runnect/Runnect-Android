package com.runnect.runnect.util.analytics

object EventName {
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