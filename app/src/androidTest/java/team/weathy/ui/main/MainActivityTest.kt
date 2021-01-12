package team.weathy.ui.main

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.FlowPreview
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import team.weathy.IdlingRegistryRule
import team.weathy.MainApplication
import team.weathy.util.PixelRatio
import team.weathy.util.SPUtil
import javax.inject.Inject

@FlowPreview
@HiltAndroidTest
class MainActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val idlingRule = IdlingRegistryRule()

    @Inject
    lateinit var spUtil: SPUtil

    @Before
    fun init() {
        MainApplication.pixelRatio = PixelRatio(ApplicationProvider.getApplicationContext())
        hiltRule.inject()
    }

    @Test
    fun navigate_SearchFragment() {
        ActivityScenario.launch(MainActivity::class.java)
    }
}