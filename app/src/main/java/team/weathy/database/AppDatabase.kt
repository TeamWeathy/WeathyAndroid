package team.weathy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import team.weathy.model.entity.RecentSearchCode

@Database(entities = [RecentSearchCode::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchCodeDao(): RecentSearchCodeDao
}