# ===========================
# Runnect ProGuard/R8 Rules
# ===========================
# 각 라이브러리의 공식 문서 또는 공식 저장소에 근거한 규칙만 포함합니다.
# consumer rules로 자동 적용되는 라이브러리는 별도 규칙을 추가하지 않습니다.
# (Retrofit, OkHttp, Kotlin Serialization, Glide, Naver Map SDK, DataBinding, Hilt, Gson)

# --- Firebase Crashlytics ---
# 난독화된 스택 트레이스를 읽을 수 있도록 소스 파일명/라인 번호 유지
# 공식 문서: https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# --- DTO ---
# Gson 리플렉션으로 역직렬화되는 DTO 클래스의 필드명 보존
# (BaseResponse, ErrorResponse 등이 Gson과 Kotlin Serialization 양쪽에서 사용됨)
-keepclassmembers class com.runnect.runnect.data.dto.** { <fields>; }

# --- Retrofit + kotlin.Result ---
# Retrofit이 리턴 타입의 제네릭 정보를 리플렉션으로 읽으므로 Signature 유지 필요
# kotlin.Result는 inline class라 R8이 타입 정보를 최적화할 수 있음
# https://github.com/square/retrofit/issues/3880
-keep class kotlin.Result { *; }
-keepattributes Signature

# --- Kakao SDK ---
# 공식 문서: https://developers.kakao.com/docs/latest/en/android/getting-started#configure-for-shrinking-and-obfuscation-(optional)
-keep class com.kakao.sdk.**.model.* { <fields>; }
