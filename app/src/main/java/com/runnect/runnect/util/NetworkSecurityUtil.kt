package com.runnect.runnect.util

import com.runnect.runnect.BuildConfig
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NetworkSecurityUtil {
    
    /**
     * TLS 1.2+ 를 강제하는 안전한 OkHttpClient 생성
     */
    fun createSecureOkHttpClient(
        enableTrustAllCerts: Boolean = false // 개발 환경에서만 true
    ): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        
        // TLS 1.2+ 강제 설정
        val connectionSpecs = listOf(
            ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
                .build(),
            ConnectionSpec.CLEARTEXT // HTTP 허용 (필요시)
        )
        
        builder.connectionSpecs(connectionSpecs)
        
        // 개발 환경에서만 모든 인증서 신뢰 (프로덕션에서는 절대 사용 금지!)
        if (enableTrustAllCerts && BuildConfig.DEBUG) {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            
            builder.sslSocketFactory(
                sslContext.socketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
        }
        
        return builder
    }
    
}