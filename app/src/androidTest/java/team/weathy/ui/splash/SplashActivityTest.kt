package team.weathy.ui.splash

import android.app.Application
import android.content.SharedPreferences
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.FlowPreview
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import team.weathy.IdlingRegistryRule
import team.weathy.MainApplication
import team.weathy.di.PermanentStorageModule
import team.weathy.di.UniqueIdentifierModule
import team.weathy.ui.landing.LandingActivity
import team.weathy.ui.main.MainActivity
import team.weathy.ui.nicknameset.NicknameSetActivity
import team.weathy.util.PixelRatio
import team.weathy.util.SPUtil
import team.weathy.util.UniqueIdentifier
import team.weathy.util.getCurrentActivity


@FlowPreview
@HiltAndroidTest
@UninstallModules(PermanentStorageModule::class, UniqueIdentifierModule::class)
class SplashActivityTest {
    private var isUniqueIdExist = false

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val idlingRule = IdlingRegistryRule()

    @BindValue
    @JvmField
    val spUtil: SPUtil = object : SPUtil {
        override val sharedPreferences: SharedPreferences
            get() = ApplicationProvider.getApplicationContext<Application>().getSharedPreferences("hi", 0)
        override var isFirstLaunch: Boolean = false
    }

    @BindValue
    @JvmField
    val uniqueId: UniqueIdentifier = object : UniqueIdentifier {
        override val id: String
            get() = ""
        override val exist: Boolean
            get() = isUniqueIdExist

        override fun generate(): String = ""

        override fun save(uuid: String) = false
    }

    @Before
    fun init() {
        MainApplication.pixelRatio = PixelRatio(ApplicationProvider.getApplicationContext())
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        spUtil.isFirstLaunch = false
        isUniqueIdExist = false
    }

    @Test
    fun test_first_launch_then_navigate_landing() {
        spUtil.isFirstLaunch = true
        ActivityScenario.launch(SplashActivity::class.java)
        Truth.assertThat(getCurrentActivity()).isInstanceOf(LandingActivity::class.java)
    }

    @Test
    fun test_not_first_launch_and_unique_id_doesnt_exist_then_navigate_nickname_set() {
        spUtil.isFirstLaunch = false
        isUniqueIdExist = false
        ActivityScenario.launch(SplashActivity::class.java)
        Truth.assertThat(getCurrentActivity()).isInstanceOf(NicknameSetActivity::class.java)
    }

    @Test
    fun test_not_first_launch_and_unique_id_exists_then_navigate_main() {
        spUtil.isFirstLaunch = false
        isUniqueIdExist = true
        ActivityScenario.launch(SplashActivity::class.java)
        Truth.assertThat(getCurrentActivity()).isInstanceOf(MainActivity::class.java)
    }
}