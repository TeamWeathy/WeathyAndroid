package team.weathy

import androidx.test.espresso.IdlingRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import team.weathy.util.__TEST__IDLING__

class IdlingRegistryRule : TestWatcher() {
    private val idlingResources = __TEST__IDLING__.countingIdlingResources

    override fun starting(description: Description?) {
        super.starting(description)
        IdlingRegistry.getInstance().register(idlingResources)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        IdlingRegistry.getInstance().unregister(idlingResources)
    }
}