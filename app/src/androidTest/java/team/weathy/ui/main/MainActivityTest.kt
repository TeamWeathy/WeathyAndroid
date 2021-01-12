package team.weathy.ui.main

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
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
import team.weathy.util.debugE
import javax.inject.Inject

@FlowPreview
@HiltAndroidTest
class MainActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val idlingRule = IdlingRegistryRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    val scenario
        get() = activityRule.scenario

    @Inject
    lateinit var spUtil: SPUtil

    @Before
    fun init() {
        MainApplication.pixelRatio = PixelRatio(ApplicationProvider.getApplicationContext())
        hiltRule.inject()
    }

    @Test
    fun test() {
        debugE(spUtil)
    }
}