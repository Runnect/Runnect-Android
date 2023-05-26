package com.runnect.runnect.di

import com.runnect.runnect.data.api.*
import com.runnect.runnect.data.repository.*
import com.runnect.runnect.data.source.remote.*
import com.runnect.runnect.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl(
            UserDataSource(
                PApiClient.getApiClient()!!.create(PUserService::class.java)
            )
        )
    }

    @Singleton
    @Provides
    fun provideCourseRepository(): CourseRepository {
        return CourseRepositoryImpl(
            CourseDataSource(
                PApiClient.getApiClient()!!.create(PCourseService::class.java)
            )
        )
    }

    @Singleton
    @Provides
    fun provideDepartureSearchRepository(): DepartureSearchRepository {
        return DepartureSearchRepositoryImpl(
            DepartureSearchDataSource(
                KApiSearch.getRetrofit().create(KSearchService::class.java)
            )
        )
    }

    @Singleton
    @Provides
    fun provideLoginRepository(): LoginRepository {
        return LoginRepositoryImpl(
            LoginDataSource(
                PApiClient.getApiClient()!!.create(LoginService::class.java)
            )
        )
    }

    @Singleton
    @Provides
    fun provideStorageRepository(): StorageRepository {
        return StorageRepositoryImpl(
            StorageDataSource(
                KApiCourse.getApiClient()!!.create(KCourseService::class.java)
            )
        )
    }
}