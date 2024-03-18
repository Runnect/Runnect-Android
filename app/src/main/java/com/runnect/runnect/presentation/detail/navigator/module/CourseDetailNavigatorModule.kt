package com.runnect.runnect.presentation.detail.navigator.module

import com.runnect.runnect.navigator.feature.detail.CourseDetailNavigator
import com.runnect.runnect.presentation.detail.navigator.impl.CourseDetailNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CourseDetailNavigatorModule {

    @Singleton
    @Binds
    abstract fun bindDetailNavigator(navigator: CourseDetailNavigatorImpl): CourseDetailNavigator
}