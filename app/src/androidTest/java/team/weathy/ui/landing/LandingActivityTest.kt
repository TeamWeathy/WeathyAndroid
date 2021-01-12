package team.weathy.ui.landing

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import team.weathy.IdlingRegistryRule
import team.weathy.MainApplication
import team.weathy.R.id
import team.weathy.ui.nicknameset.NicknameSetActivity
import team.weathy.util.PixelRatio
import team.weathy.util.debugE
import team.weathy.util.getCurrentActivity


@HiltAndroidTest
class LandingActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val idlingRule = IdlingRegistryRule()

    @Before
    fun setup() {
        MainApplication.pixelRatio = PixelRatio(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun swipe_pagers_and_click_button_then_navigate_nickname_set() {
        ActivityScenario.launch(LandingActivity::class.java)

        Truth.assertThat(getCurrentActivity()).isInstanceOf(LandingActivity::class.java)

        onView(withId(id.pager)).perform(ViewActions.swipeLeft())
        onView(withId(id.pager)).perform(ViewActions.swipeLeft())

        onView(ViewMatchers.withText("웨디에서 사용할\n닉네임을 입력해주세요.")).check(ViewAssertions.doesNotExist())
        onView(withId(id.start)).perform(ViewActions.click())
        onView(ViewMatchers.withText("웨디에서 사용할\n닉네임을 입력해주세요.")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Truth.assertThat(getCurrentActivity()).isInstanceOf(NicknameSetActivity::class.java)

        debugE(getCurrentActivity().application)
    }
}