package team.weathy.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import team.weathy.model.entity.RecentSearchCode

class RecentSearchCodeDaoTest {
    private lateinit var dao: RecentSearchCodeDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        dao = db.recentSearchCodeDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun add() = runBlocking {
        Truth.assertThat(dao.getAll()).hasSize(0)

        val codes = listOf(RecentSearchCode(1), RecentSearchCode(2), RecentSearchCode(3))
        codes.forEach {
            dao.add(it)
        }
        // 1
        // 2 1
        // 3 2 1

        Truth.assertThat(dao.getAll()).hasSize(3)
        dao.add(codes.first())
        // 1 3 2

        Truth.assertThat(dao.getAll()).hasSize(3)

        Truth.assertThat(dao.getAll()[0].code).isEqualTo(1)
        Truth.assertThat(dao.getAll()[1].code).isEqualTo(3)
        Truth.assertThat(dao.getAll()[2].code).isEqualTo(2)
    }
}