package com.runnect.runnect.di

import com.runnect.runnect.data.service.*
import com.runnect.runnect.data.repository.*
import com.runnect.runnect.data.source.remote.*
import com.runnect.runnect.domain.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    fun bindCourseRepository(courseRepositoryImpl: CourseRepositoryImpl): CourseRepository

    @Singleton
    @Binds
    fun bindDepartureSearchRepository(departureSearchRepositoryImpl: DepartureSearchRepositoryImpl): DepartureSearchRepository

    @Singleton
    @Binds
    fun bindStorageRepository(storageRepositoryImpl: StorageRepositoryImpl): StorageRepository

    @Singleton
    @Binds
    fun bindLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository

    @Singleton
    @Binds
    fun bindBannerRepository(bannerRepositoryImpl: BannerRepositoryImpl): BannerRepository

}