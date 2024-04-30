package com.runnect.runnect.util.preference.StatusType

enum class LoginStatus(val value: String) {
    EXPIRED(""), // 토큰 만료
    NONE("none"), // 탈퇴, 로그아웃
    VISITOR("visitor"), // 방문자 모드
    LOGGED_IN(""); // 나머지 경우 -> 자동로그인

    companion object {
        fun getLoginStatus(value: String?): LoginStatus {
            return when (value) {
                EXPIRED.value -> EXPIRED
                NONE.value -> NONE
                VISITOR.value -> VISITOR
                else -> LOGGED_IN
            }
        }
    }
}
