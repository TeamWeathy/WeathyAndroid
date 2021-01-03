package team.weathy.util

import java.util.*

data class DateTime(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
) {
    private val calendar = getSeoulTimeZoneCalendarInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR, hour)
        set(Calendar.MINUTE, minute)
    }


    /**
     * 1 - 7 (SUN - MON)
     */
    fun dayOfWeek(): Int {
        return calendar[Calendar.DAY_OF_WEEK]
    }

    fun weekOfMonth(): Int {
        return calendar[Calendar.WEEK_OF_MONTH]
    }

    companion object {
        fun now(): DateTime {
            return getSeoulTimeZoneCalendarInstance().let { calendar ->
                DateTime(
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH] + 1,
                    calendar[Calendar.DAY_OF_MONTH],
                    calendar[Calendar.HOUR],
                    calendar[Calendar.MINUTE],
                )
            }
        }

        private fun getSeoulTimeZoneCalendarInstance() =Calendar.getInstance(TimeZone.getTimeZone("Asia/seoul"))
    }
}