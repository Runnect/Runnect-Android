package com.runnect.runnect.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.runnect.runnect.data.repository.*
import com.runnect.runnect.data.service.*
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
    fun provideBannerService() = Firebase.firestore
}