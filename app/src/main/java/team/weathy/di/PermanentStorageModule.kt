package team.weathy.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import team.weathy.util.SPUtil
import team.weathy.util.SPUtilImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class PermanentStorageModule {
    @Binds
    @Singleton
    abstract fun bindSPUtil(spUtil: SPUtilImpl): SPUtil
}