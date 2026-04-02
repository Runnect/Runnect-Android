package com.runnect.runnect.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.runnect.runnect.data.service.CourseService
import com.runnect.runnect.data.service.LoginService
import com.runnect.runnect.data.service.ReverseGeocodingService
import com.runnect.runnect.data.service.SearchService
import com.runnect.runnect.data.service.UserService
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
    fun providePUserService(@RetrofitModule.RetrofitV2 retrofitV2: Retrofit) =
        retrofitV2.create(UserService::class.java)

    @Singleton
    @Provides
    fun providePLoginService(@RetrofitModule.RetrofitV2 retrofitV2: Retrofit) =
        retrofitV2.create(LoginService::class.java)

    @Singleton
    @Provides
    fun providePCourseService(@RetrofitModule.RetrofitV2 retrofitV2: Retrofit) =
        retrofitV2.create(CourseService::class.java)

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
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore
}