package com.runnect.runnect.di

import com.runnect.runnect.data.service.*
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
    fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository = userRepositoryImpl

    @Singleton
    @Provides
    fun provideCourseRepository(courseRepositoryImpl: CourseRepositoryImpl): CourseRepository = courseRepositoryImpl

    @Singleton
    @Provides
    fun provideDepartureSearchRepository(departureSearchRepositoryImpl: DepartureSearchRepositoryImpl): DepartureSearchRepository = departureSearchRepositoryImpl

    @Singleton
    @Provides
    fun provideStorageRepository(storageRepositoryImpl: StorageRepositoryImpl): StorageRepository = storageRepositoryImpl

    @Singleton
    @Provides
    fun provideLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository = loginRepositoryImpl

    @Singleton
    @Provides
    fun provideBannerRepository(bannerRepositoryImpl: BannerRepositoryImpl): BannerRepository = bannerRepositoryImpl

}