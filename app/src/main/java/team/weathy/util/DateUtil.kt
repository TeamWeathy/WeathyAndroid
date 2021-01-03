package team.weathy.util

import java.util.*

data class DateTime(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
)

object DateUtil {
    fun now(): DateTime {
        return Calendar.getInstance(TimeZone.getTimeZone("asia/seoul")).let {
            DateTime(
                it[Calendar.YEAR],
                it[Calendar.MONTH] + 1,
                it[Calendar.DAY_OF_MONTH],
                it[Calendar.HOUR],
                it[Calendar.MINUTE],
            )
        }
    }
}