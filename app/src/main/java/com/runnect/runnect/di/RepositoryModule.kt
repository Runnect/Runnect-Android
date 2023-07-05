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
    fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl) = userRepositoryImpl

    @Singleton
    @Provides
    fun provideCourseRepository(courseRepositoryImpl: CourseRepositoryImpl) = courseRepositoryImpl

    @Singleton
    @Provides
    fun provideDepartureSearchRepository(departureSearchRepositoryImpl: DepartureSearchRepositoryImpl) = departureSearchRepositoryImpl

    @Singleton
    @Provides
    fun provideStorageRepository(storageRepositoryImpl: StorageRepositoryImpl) = storageRepositoryImpl

    @Singleton
    @Provides
    fun provideLoginRepository(loginRepositoryImpl: LoginRepositoryImpl) = loginRepositoryImpl
}