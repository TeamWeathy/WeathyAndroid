package team.weathy.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import team.weathy.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "MainDatabase").fallbackToDestructiveMigration()
            .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun provideRecentSearchCodeDao(db: AppDatabase) = db.recentSearchCodeDao()
}