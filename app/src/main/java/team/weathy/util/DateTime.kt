package team.weathy.util

import java.util.*

data class DateTime(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
) {
    private val calendar = getCalendarInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }

    fun weekOfMonth(): Int {
        return calendar[Calendar.WEEK_OF_MONTH]
    }

    companion object {
        fun now() = newInstance(getCalendarInstance())
        fun nowJoda(): org.joda.time.DateTime {
            val now = now()
            return org.joda.time.DateTime(
                now.year,
                now.month,
                now.day,
                now.hour,
                now.minute,
            )
        }

        private fun newInstance(calendar: Calendar) = DateTime(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH] + 1,
            calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
        )

        fun getCalendarInstance(): Calendar = Calendar.getInstance()
    }
}