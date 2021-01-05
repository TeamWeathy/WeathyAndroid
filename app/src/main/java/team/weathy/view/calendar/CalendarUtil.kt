package team.weathy.view.calendar

import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import team.weathy.R

object CalendarUtil {
    @ColorRes
    fun getColorFromWeek(@IntRange(from = 0L, to = 6L) week: Int) = when (week % 7) {
        6 -> R.color.blue_temp
        0 -> R.color.red_temp
        else -> R.color.main_grey
    }
}