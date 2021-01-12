package team.weathy.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import team.weathy.util.UniqueIdentifier
import team.weathy.util.UniqueIdentifierImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class UniqueIdentifierModule {
    @Binds
    @Singleton
    abstract fun bindUniqueIndentifier(uniqueId: UniqueIdentifierImpl): UniqueIdentifier
}