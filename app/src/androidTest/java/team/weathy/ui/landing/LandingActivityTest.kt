package team.weathy.ui.landing

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
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
import team.weathy.R.id
import team.weathy.di.UniqueIdentifierModule
import team.weathy.ui.main.MainActivity
import team.weathy.ui.nicknameset.NicknameSetActivity
import team.weathy.util.PixelRatio
import team.weathy.util.UniqueIdentifier
import team.weathy.util.getCurrentActivity


@FlowPreview
@HiltAndroidTest
@UninstallModules(UniqueIdentifierModule::class)
class LandingActivityTest {
    private var isUniqueIdExist = false

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val idlingRule = IdlingRegistryRule()

    @BindValue
    @JvmField
    val uniqueId: UniqueIdentifier = object : UniqueIdentifier {
        override val id: String? = null
        override val exist
            get() = isUniqueIdExist

        override fun generate() = ""
        override fun save(uuid: String) = false
    }

    @Before
    fun setup() {
        MainApplication.pixelRatio = PixelRatio(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        isUniqueIdExist = false
    }

    @Test
    fun swipe_pagers_and_click_button_then_navigate_nickname_set() {
        isUniqueIdExist = false

        ActivityScenario.launch(LandingActivity::class.java)

        Truth.assertThat(getCurrentActivity()).isInstanceOf(LandingActivity::class.java)

        swipeAndStart()
        onView(ViewMatchers.withText("웨디에서 사용할\n닉네임을 입력해주세요.")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Truth.assertThat(getCurrentActivity()).isInstanceOf(NicknameSetActivity::class.java)
    }

    @Test
    fun swipe_pagers_and_click_button_then_navigate_main() {
        isUniqueIdExist = true

        ActivityScenario.launch(LandingActivity::class.java)

        Truth.assertThat(getCurrentActivity()).isInstanceOf(LandingActivity::class.java)

        swipeAndStart()

        Truth.assertThat(getCurrentActivity()).isInstanceOf(MainActivity::class.java)
    }

    private fun swipeAndStart() {
        onView(withId(id.pager)).perform(swipeLeft(), swipeLeft())
        onView(withId(id.start)).perform(click())
    }
}