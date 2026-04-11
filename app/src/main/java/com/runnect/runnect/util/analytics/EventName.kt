package com.runnect.runnect.util.analytics

object EventName {

    // App Start / Onboarding

    // App
    const val EVENT_VIEW_HOME = "view_home"
    const val EVENT_VIEW_SOCIAL_LOGIN = "view_social_login"

    // Login
    const val EVENT_CLICK_GOOGLE_LOGIN = "click_google_login"
    const val EVENT_CLICK_KAKAO_LOGIN = "click_kakao_login"
    const val EVENT_CLICK_VISITOR = "click_visitor"

    // Phase 2: Login result
    const val ACTION_LOGIN_SUCCESS = "action_login_success"
    const val ACTION_LOGIN_FAIL = "action_login_fail"

    // Phase 2: Onboarding
    const val VIEW_GIVE_NICKNAME = "view_give_nickname"
    const val ACTION_NICKNAME_COMPLETE = "action_nickname_complete"

    // Phase 3: Onboarding
    const val CLICK_NICKNAME_SKIP = "click_nickname_skip"

    // Phase 1: App start
    const val SYS_APP_OPEN = "sys_app_open"

    // Visitor Mode
    const val EVENT_CLICK_JOIN_IN_COURSE_DRAWING = "click_join_in_course_drawing"
    const val EVENT_CLICK_JOIN_IN_COURSE_DISCOVERY = "click_join_in_course_discovery"
    const val EVENT_CLICK_JOIN_IN_STORAGE = "click_join_in_storage"
    const val EVENT_CLICK_JOIN_IN_MY_PAGE = "click_join_in_my_page"

    // Course Drawing

    const val EVENT_CLICK_COURSE_DRAWING = "click_course_drawing"
    const val EVENT_CLICK_CURRENT_LOCATE = "click_current_locate"
    const val EVENT_CLICK_MAP_LOCATE = "click_map_locate"
    const val EVENT_CLICK_STORED_AFTER_COURSE_COMPLETE = "click_stored_after_course_complete"
    const val EVENT_CLICK_RUN_AFTER_COURSE_COMPLETE = "click_run_after_course_complete"

    // Phase 1: Course drawing flow
    const val VIEW_COURSE_DRAWING = "view_course_drawing"
    const val ACTION_COURSE_DRAWING_COMPLETE = "action_course_drawing_complete"
    const val VIEW_COURSE_COMPLETE_RESULT = "view_course_complete_result"

    // Phase 2: Course drawing detail
    const val ACTION_COURSE_DRAWING_START = "action_course_drawing_start"
    const val CLICK_SHARE_AFTER_COURSE_COMPLETE = "click_share_after_course_complete"

    // Running

    // Existing
    const val EVENT_CLICK_BACK_RUNNING_TRACKING = "click_back_running_tracking"
    const val EVENT_CLICK_STORE_RUNNING_TRACKING = "click_store_running_tracking"

    // Phase 1: Countdown
    const val VIEW_COUNTDOWN = "view_countdown"
    const val CLICK_CANCEL_COUNTDOWN = "click_cancel_countdown"

    // Phase 1: Running progress
    const val ACTION_RUN_START = "action_run_start"
    const val ACTION_RUN_PAUSE = "action_run_pause"
    const val ACTION_RUN_RESUME = "action_run_resume"
    const val ACTION_RUN_COMPLETE = "action_run_complete"
    const val ACTION_RUN_ABANDON = "action_run_abandon"

    // Phase 1: End run
    const val VIEW_END_RUN = "view_end_run"

    // Phase 2: End run actions
    const val CLICK_SAVE_RUN_RECORD = "click_save_run_record"
    const val CLICK_SHARE_RUN_RECORD = "click_share_run_record"
    const val CLICK_RUN_AGAIN = "click_run_again"

    // Course Discovery

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

    // Phase 1: Course detail -> Run
    const val CLICK_RUN_FROM_DETAIL = "click_run_from_detail"

    // Phase 2: Course discovery detail
    const val ACTION_COURSE_SEARCH_EXECUTE = "action_course_search_execute"
    const val VIEW_DISCOVER_PICK = "view_discover_pick"
    const val CLICK_UNSCRAP = "click_unscrap"
    const val ACTION_COURSE_UPLOAD_COMPLETE = "action_course_upload_complete"

    // Storage

    const val EVENT_CLICK_MY_DRAW_STORAGE_COURSE_DRAWING_START =
        "click_my_storage_course_drawing_start"
    const val EVENT_CLICK_SCRAP_COURSE = "click_scrap_course"
    const val EVENT_MY_STORAGE_TRY_REMOVE = "click_my_storage_try_remove"

    // Phase 1: Storage -> Run
    const val CLICK_RUN_FROM_STORAGE = "click_run_from_storage"

    // Phase 2: Storage views
    const val VIEW_STORAGE_MY_DRAW = "view_storage_my_draw"
    const val VIEW_STORAGE_SCRAP = "view_storage_scrap"
    const val CLICK_MY_DRAW_COURSE = "click_my_draw_course"
    const val VIEW_MY_DRAW_DETAIL = "view_my_draw_detail"

    // Navigation Menu

    const val EVENT_CLICK_NAV_COURSE_DRAWING = "click_course_drawing_tab_bar"
    const val EVENT_CLICK_NAV_COURSE_DISCOVERY = "click_course_discovery_tab_bar"
    const val EVENT_CLICK_NAV_STORAGE = "click_storage_tab_bar"
    const val EVENT_CLICK_NAV_MY_PAGE = "click_my_page_tab_bar"

    // MyPage

    const val EVENT_CLICK_RUNNING_RECORD = "click_running_record"
    const val EVENT_CLICK_GOAL_REWARD = "click_goal_reward"
    const val EVENT_CLICK_UPLOADED_COURSE = "click_uploaded_course"

    // MyHistory
    const val EVENT_CLICK_COURSE_DRAWING_IN_RUNNING_RECORD =
        "click_course_drawing_in_running_record"

    // Phase 2: MyPage detail
    const val VIEW_MY_HISTORY_DETAIL = "view_my_history_detail"
    const val CLICK_RUN_AGAIN_FROM_HISTORY = "click_run_again_from_history"

    // Phase 3: MyPage
    const val VIEW_MY_REWARD = "view_my_reward"
    const val VIEW_EDIT_PROFILE = "view_edit_profile"
    const val ACTION_EDIT_PROFILE_COMPLETE = "action_edit_profile_complete"

    // MyUpload
    const val EVENT_CLICK_COURSE_UPLOAD_IN_UPLOADED_COURSE =
        "click_course_upload_in_uploaded_course"

    // Settings / System

    const val EVENT_VIEW_SUCCESS_LOGOUT = "view_success_logout"
    const val EVENT_CLICK_TRY_LOGOUT = "click_try_logout"
    const val EVENT_VIEW_SUCCESS_WITHDRAW = "view_success_withdraw"
    const val EVENT_CLICK_TRY_WITHDRAW = "click_try_withdraw"

    // Phase 2: System
    const val SYS_DEEPLINK_OPEN = "sys_deeplink_open"

    // Parameter Keys

    object Param {
        const val SOURCE = "source"
        const val COURSE_ID = "course_id"
        const val DISTANCE_M = "distance_m"
        const val POINT_COUNT = "point_count"
        const val DRAWING_TIME_SEC = "drawing_time_sec"
        const val DEPARTURE_NAME = "departure_name"
        const val ELAPSED_SEC = "elapsed_sec"
        const val TOTAL_TIME_SEC = "total_time_sec"
        const val TOTAL_DISTANCE_M = "total_distance_m"
        const val AVG_PACE_SEC_PER_KM = "avg_pace_sec_per_km"
        const val PAUSE_COUNT = "pause_count"
        const val TOTAL_PAUSE_SEC = "total_pause_sec"
        const val PAUSE_DURATION_SEC = "pause_duration_sec"
        const val COMPLETION_RATE = "completion_rate"
        const val ABANDON_REASON = "abandon_reason"
        const val TARGET_DISTANCE_M = "target_distance_m"
        const val COUNTDOWN_SEC_REMAINING = "countdown_sec_remaining"
        const val PACE_SEC_PER_KM = "pace_sec_per_km"
        const val METHOD = "method"
        const val IS_NEW_USER = "is_new_user"
        const val ERROR_CODE = "error_code"
        const val NICKNAME_LENGTH = "nickname_length"
        const val SHARE_TARGET = "share_target"
        const val KEYWORD = "keyword"
        const val RESULT_COUNT = "result_count"
        const val STORAGE_TYPE = "storage_type"
        const val DAYS_SINCE_CREATED = "days_since_created"
        const val COURSE_COUNT = "course_count"
        const val RECORD_ID = "record_id"
        const val TOTAL_REWARDS = "total_rewards"
        const val CHANGED_FIELDS = "changed_fields"
        const val LAUNCH_TYPE = "launch_type"
        const val REFERRER = "referrer"
        const val DEEPLINK_URL = "deeplink_url"
        const val TARGET_SCREEN = "target_screen"
        const val SCREEN_NAME = "screen_name"
        const val LAST_ACTION = "last_action"
        const val EXCEPTION_TYPE = "exception_type"
        const val STACK_TRACE_HASH = "stack_trace_hash"
        const val WITHDRAW_REASON = "withdraw_reason"
        const val DAYS_SINCE_SIGNUP = "days_since_signup"
        const val TOTAL_RUNS = "total_runs"
        const val TOTAL_COURSES = "total_courses"
        const val HAS_DESCRIPTION = "has_description"
    }
}
