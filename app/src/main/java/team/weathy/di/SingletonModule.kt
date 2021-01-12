package team.weathy.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import team.weathy.util.location.LocationUtil
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class SingletonModule {
    @Provides
    @Singleton
    @ApplicationContext
    fun provideApplication(app: Application) = app

    @Provides
    @Singleton
    fun provideLocationUtll(@ApplicationContext context: Application) = LocationUtil(context)
}