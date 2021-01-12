package team.weathy

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import team.weathy.database.AppDatabase
import team.weathy.database.RecentSearchCodeDao
import team.weathy.model.entity.RecentSearchCode

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], application = HiltTestApplication::class)
class RecentSearchCodeDaoTest {
    private lateinit var dao: RecentSearchCodeDao
    private lateinit var db: AppDatabase

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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