package team.weathy.util

import androidx.test.espresso.idling.CountingIdlingResource

object __TEST__IDLING__ {
    private const val RESOURCES = "GLOBAL"

    @JvmField
    val countingIdlingResources = CountingIdlingResource(RESOURCES)

    fun increment() {
        countingIdlingResources.increment()
    }

    fun decrement() {
        if (!countingIdlingResources.isIdleNow) countingIdlingResources.decrement()
    }
}