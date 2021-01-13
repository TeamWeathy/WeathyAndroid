package team.weathy.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import team.weathy.model.entity.RecentSearchCode

@Dao
interface RecentSearchCodeDao {
    @Query("SELECT * FROM RecentSearchCode ORDER BY id DESC")
    suspend fun getAll(): List<RecentSearchCode>

    @Insert
    suspend fun addInternal(item: RecentSearchCode)

    @Query("DELETE FROM RecentSearchCode")
    suspend fun drop()

    @Query("DELETE FROM RecentSearchCode WHERE :code == code")
    suspend fun delete(code: Long)

    @Transaction
    suspend fun add(item: RecentSearchCode) {
        val items = getAll()
        val newItems = (listOf(item) + items).distinct().take(3)
        drop()
        newItems.forEach {
            addInternal(it)
        }
    }
}