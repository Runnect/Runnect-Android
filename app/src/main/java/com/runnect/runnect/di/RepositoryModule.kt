package com.runnect.runnect.di

import com.runnect.runnect.data.api.*
import com.runnect.runnect.data.repository.CourseRepositoryImpl
import com.runnect.runnect.data.repository.DepartureSearchRepositoryImpl
import com.runnect.runnect.data.repository.UserRepositoryImpl
import com.runnect.runnect.data.source.remote.CourseDataSource
import com.runnect.runnect.data.source.remote.DepartureSearchDataSource
import com.runnect.runnect.data.source.remote.UserDataSource
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.domain.DepartureSearchRepository
import com.runnect.runnect.domain.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.create
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
    fun provideCourseRepository():CourseRepository{
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
}