package com.runnect.runnect.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.runnect.runnect.data.repository.*
import com.runnect.runnect.data.service.*
import com.runnect.runnect.data.service.v2.UserV2Service
import com.runnect.runnect.data.source.remote.*
import com.runnect.runnect.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    /** -------- v2 -------- */
    @Singleton
    @Provides
    fun providePUserV2Service(@RetrofitModule.RetrofitV2 retrofitV2: Retrofit) =
        retrofitV2.create(UserV2Service::class.java)

    @Singleton
    @Provides
    fun providePCourseV2Service(@RetrofitModule.RetrofitV2 retrofitV2: Retrofit) =
        retrofitV2.create(CourseV2Service::class.java)

    /** -------- v1 -------- */
    @Singleton
    @Provides
    fun providePCourseService(@RetrofitModule.Runnect runnectRetrofit: Retrofit) =
        runnectRetrofit.create(CourseService::class.java)

    @Singleton
    @Provides
    fun providePUserService(@RetrofitModule.Runnect runnectRetrofit: Retrofit) =
        runnectRetrofit.create(UserService::class.java)

    @Singleton
    @Provides
    fun provideLoginService(@RetrofitModule.Runnect runnectRetrofit: Retrofit) =
        runnectRetrofit.create(LoginService::class.java)

    @Singleton
    @Provides
    fun provideKSearchService(@RetrofitModule.Tmap tmapRetrofit: Retrofit) =
        tmapRetrofit.create(SearchService::class.java)

    @Singleton
    @Provides
    fun provideReverseGeocodingService(@RetrofitModule.Tmap tmapRetrofit: Retrofit) =
        tmapRetrofit.create(ReverseGeocodingService::class.java)

    @Singleton
    @Provides
    fun provideBannerService() = Firebase.firestore
}