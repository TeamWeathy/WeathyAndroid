package team.weathy.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
//    @Provides
    //    @Singleton
    //    fun provideDatabase(@ApplicationContext context: Context) =
    //        Room.databaseBuilder(context, AppDatabase::class.java, "MainDatabase").allowMainThreadQueries().build()
    //
    //    @Provides
    //    @Singleton
    //    fun provideRecentSearchCodeDao(db: AppDatabase) = db.recentSearchCodeDao()
}