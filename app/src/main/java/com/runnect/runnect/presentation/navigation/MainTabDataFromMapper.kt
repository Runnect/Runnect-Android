package com.runnect.runnect.presentation.navigation

/**
 * EndRunActivity가 읽는 CourseData.dataFrom(String) 값을 MainTab으로 변환한다.
 * CourseData.dataFrom의 타입을 바꾸는 건 DrawActivity/CourseDetailActivity/MyDrawDetailActivity까지
 * 연쇄적으로 건드리게 되어 스코프 밖이라, 여기서만 국소적으로 매핑한다.
 * MainActivity.checkIntentValue()의 기존 when 분기를 그대로 옮긴 것이라 동작 변화는 없다 —
 * "detail"처럼 기존에도 매칭되지 않던 값은 그대로 매칭 없음(null, 기본 그리기 탭)으로 남는다.
 */
fun mapDataFromToMainTab(dataFrom: String?): MainTab? = when (dataFrom) {
    "fromDrawCourse", "fromDeleteMyDrawDetail", "fromMyDrawDetail" -> MainTab.STORAGE
    "fromMyScrap", "fromCourseDetail" -> MainTab.DISCOVER
    else -> null
}
