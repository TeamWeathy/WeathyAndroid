package team.weathy.util

import android.app.Activity
import android.view.View
import androidx.annotation.ColorInt

data class StatusBarState(
    @ColorInt val color: Int,
    val layoutConsumed: Boolean,
)

object StatusBarUtil {
    fun collapseStatusBar(activity: Activity) {
        activity.window?.decorView?.run {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun expandStatusBar(activity: Activity) {
        activity.window?.decorView?.run {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}