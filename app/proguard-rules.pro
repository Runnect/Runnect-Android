# ===========================
# Runnect ProGuard/R8 Rules
# ===========================
# 각 라이브러리의 공식 문서 또는 공식 저장소에 근거한 규칙만 포함합니다.
# consumer rules로 자동 적용되는 라이브러리는 별도 규칙을 추가하지 않습니다.
# (Retrofit, OkHttp, Kotlin Serialization, Glide, Naver Map SDK, DataBinding, Hilt)

# --- Firebase Crashlytics ---
# 난독화된 스택 트레이스를 읽을 수 있도록 소스 파일명/라인 번호 유지
# 공식 문서: https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-renamesourcefileattribute SourceFile

# --- Kakao SDK ---
# 공식 문서: https://developers.kakao.com/docs/latest/en/android/getting-started#configure-for-shrinking-and-obfuscation-(optional)
-keep class com.kakao.sdk.**.model.* { <fields>; }

# --- Gson ---
# Gson 2.13.x JAR에 consumer rules가 포함되어 있지 않으므로 수동 추가 필요
# 공식 규칙 파일: https://github.com/google/gson/blob/main/gson/src/main/resources/META-INF/proguard/gson.pro
# 공식 트러블슈팅: https://github.com/google/gson/blob/main/Troubleshooting.md#r8-agp
-keep class * extends com.google.gson.TypeAdapter
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
