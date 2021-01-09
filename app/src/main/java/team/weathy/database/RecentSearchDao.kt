//package team.weathy.database
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Transaction
//import team.weathy.model.entity.RecentSearchCode
//
//@Dao
//interface RecentSearchCodeDao {
//    @Query("SELECT * FROM RecentSearchCode")
//    suspend fun getAll(): List<RecentSearchCode>
//
//    @Insert
//    suspend fun addInternal(item: RecentSearchCode)
//
//    @Query("DELETE FROM RecentSearchCode")
//    suspend fun drop()
//
//    @Delete
//    suspend fun delete(item: RecentSearchCode)
//
//    @Transaction
//    suspend fun add(item: RecentSearchCode) {
//        val items = getAll()
//        val newItems = (listOf(item) + items)
//        drop()
//        newItems.take(3).forEach {
//            addInternal(it)
//        }
//    }
//}