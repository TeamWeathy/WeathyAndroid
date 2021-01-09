//package team.weathy
//
//import android.content.Context
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.google.common.truth.Truth
//import kotlinx.coroutines.runBlocking
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import team.weathy.database.AppDatabase
//import team.weathy.database.RecentSearchCodeDao
//import team.weathy.model.entity.RecentSearchCode
//import team.weathy.util.debugE
//import java.io.IOException
//
//@RunWith(AndroidJUnit4::class)
//class DBTest {
//    private lateinit var dao: RecentSearchCodeDao
//    private lateinit var db: AppDatabase
//
//    @Before
//    fun createDb() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(
//            context, AppDatabase::class.java
//        ).build()
//        dao = db.recentSearchCodeDao()
//    }
//
//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        db.close()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun add() = runBlocking {
//        Truth.assertThat(dao.getAll()).hasSize(0)
//
//        val codes = listOf(RecentSearchCode(1), RecentSearchCode(2), RecentSearchCode(3))
//        codes.forEach {
//            dao.add(it)
//        }
//
//        Truth.assertThat(dao.getAll()).hasSize(3)
//
//        dao.add(codes.first())
//
//        Truth.assertThat(dao.getAll()).hasSize(3)
//
//        val result = dao.getAll(10)
//        Truth.assertThat(result[2]).isEqualTo(codes[2])
//
//        dao.add(codes[2])
//        val newResult = dao.getAll(10)
//
//        Truth.assertThat(newResult.first()).isEqualTo(codes[2])
//    }
//}