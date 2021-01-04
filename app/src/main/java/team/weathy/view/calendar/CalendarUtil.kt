package team.weathy.view.calendar

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.graphics.ColorUtils
import team.weathy.R
import team.weathy.util.extensions.clamp

object CalendarUtil {
    @ColorInt
    fun getWeekTextColor(context: Context, week: Int,animValue: Float, isToday: Boolean = false): Int {
        val weekColor = context.getColor(getWeekBaseColor(week))
        if (!isToday) return weekColor

        return ColorUtils.blendARGB(Color.WHITE, weekColor, animValue.clamp(0f, 1f))
    }

    @ColorRes
    fun getWeekBaseColor(week: Int) = when (week % 7) {
        6 -> R.color.blue_temp
        0 -> R.color.red_temp
        else -> R.color.main_grey
    }
}