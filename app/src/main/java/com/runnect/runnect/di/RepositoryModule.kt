package com.runnect.runnect.di

import com.runnect.runnect.data.api.PApiClient
import com.runnect.runnect.data.api.PUserService
import com.runnect.runnect.data.repository.UserRepositoryImpl
import com.runnect.runnect.data.source.remote.UserDataSource
import com.runnect.runnect.domain.UserRepository
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
}