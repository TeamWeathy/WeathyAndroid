package team.weathy.util

import android.app.Activity
import androidx.annotation.ColorInt


@Suppress("DEPRECATION")
object StatusBarUtil {
    fun changeColor(activity: Activity, @ColorInt color: Int) {
        activity.window?.run {
            statusBarColor = color
        }
    }
}