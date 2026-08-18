package com.runnect.runnect.di

import com.runnect.runnect.presentation.navigation.Navigator
import com.runnect.runnect.presentation.navigation.NavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {
    @Singleton
    @Binds
    fun bindNavigator(navigatorImpl: NavigatorImpl): Navigator
}
