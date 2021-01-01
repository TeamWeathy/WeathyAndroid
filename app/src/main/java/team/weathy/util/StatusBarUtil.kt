package team.weathy.util

import android.app.Activity
import android.graphics.Color
import android.view.View


object StatusBarUtil {
    fun collapseStatusBar(activity: Activity) {
        activity.window?.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }


        fun expandStatusBar(activity: Activity) {
            activity.window?.run {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = Color.WHITE
            }
        }
    }
}