package team.weathy.util

import android.app.Activity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage

fun getCurrentActivity(): Activity {
    lateinit var currentActivity: Activity
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
        for (activity in resumedActivities) {
            currentActivity = activity
            break
        }
    }
    return currentActivity
}