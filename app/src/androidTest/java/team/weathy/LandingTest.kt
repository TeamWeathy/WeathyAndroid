package team.weathy

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import team.weathy.ui.landing.LandingActivity

@RunWith(AndroidJUnit4ClassRunner::class)
class LandingTest {
    @get:Rule
    val idlingRule = IdlingRegistryRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(LandingActivity::class.java)
    val scenario
        get() = activityRule.scenario


    @Before
    fun setup() {
        IdlingRegistry.getInstance().register()
    }

    @Test
    fun test_landing_showing_success() {
        onView(withId(R.id.pager)).perform(ViewActions.swipeLeft())
        onView(withId(R.id.pager)).perform(ViewActions.swipeLeft())

        onView(ViewMatchers.withText("웨디에서 사용할\n닉네임을 입력해주세요.")).check(ViewAssertions.doesNotExist())
        onView(withId(R.id.start)).perform(ViewActions.click())
        onView(ViewMatchers.withText("웨디에서 사용할\n닉네임을 입력해주세요.")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


}