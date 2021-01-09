package team.weathy.util

import team.weathy.view.calendar.MonthlyAdapter
import team.weathy.view.calendar.WeeklyAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Calendar.*

/**
 * YYYY-MM-DD
 */
typealias DateString = String
/**
 * YYYY-MM-DDTHH
 */
typealias DateHourString = String
/**
 * YYYY-MM-DD or YYYY-MM-DDTHH
 */
typealias DateOrDateHourString = String

fun String.padZero() = padStart(2, '0')
fun Int.padZero() = toString().padZero()


val DayOfWeek.koFormat: String
    get() = when (value) {
        1 -> "월"
        2 -> "화"
        3 -> "수"
        4 -> "목"
        5 -> "금"
        6 -> "토"
        else -> "일"
    }
val Int.koFormat: String
    get() = if (this < 13) {
        "오전 ${this}시"
    } else {
        "오후 ${this - 12}시"
    }

val LocalDate.weekOfMonth: Int
    get() {
        val gc = GregorianCalendar.from(atStartOfDay(ZoneId.systemDefault()))
        gc.firstDayOfWeek = SUNDAY
        gc.minimalDaysInFirstWeek = 1
        return gc[WEEK_OF_MONTH]
    }

val LocalDate.dayOfWeekValue: Int
    get() = when (dayOfWeek.value + 1) {
        8 -> 1
        else -> dayOfWeek.value + 1
    }
val LocalDate.dayOfWeekIndex: Int
    get() = dayOfWeekValue - 1

val LocalDate.dateString: DateString
    get() = this.format(DateTimeFormatter.ISO_LOCAL_DATE)
val LocalDateTime.dateHourString: DateHourString
    get() = "${year}-${monthValue.padZero()}-${dayOfMonth.padZero()}T${hour.padZero()}"

val LocalDateTime.koFormat: String
    get() = "${monthValue.padZero()}월 ${dayOfMonth.padZero()}일 ${dayOfWeek.koFormat}요일 . ${hour.koFormat}"

fun convertMonthlyIndexToDate(index: Int): LocalDate {
    val cur = LocalDate.now()

    val diffMonth = MonthlyAdapter.MAX_ITEM_COUNT - index - 1
    return cur.minusMonths(diffMonth.toLong())
}

fun convertWeeklyIndexToDate(index: Int): LocalDate {
    val cur = LocalDate.now()

    val diffWeek = WeeklyAdapter.MAX_ITEM_COUNT - index - 1

    return cur.minusWeeks(diffWeek.toLong())
}

fun convertDateToMonthlyIndex(date: LocalDate): Int {
    val now = LocalDate.now()

    val yearDiff = now.year - date.year
    val diffIndex = now.monthValue - date.monthValue + yearDiff * 12

    return MonthlyAdapter.MAX_ITEM_COUNT - diffIndex - 1
}

fun convertDateToWeeklyIndex(date: LocalDate): Int {
    val now = LocalDate.now()

    val weekDiff = ChronoUnit.WEEKS.between(date, now).toInt()

    return WeeklyAdapter.MAX_ITEM_COUNT - weekDiff - 1
}


fun calculateRequiredRow(date: LocalDate): Int {
    return (date.lengthOfMonth() + date.withDayOfMonth(1).dayOfWeekIndex - 1) / 7 + 1
}

fun getMonthTexts(date: LocalDate): Triple<List<Int>, Int, Int> {
    val result = MutableList(42) { 0 }

    val previousMonthEndDay = date.minusMonths(1).lengthOfMonth()

    // 1 ~ 7 (MON ~ SUN)
    val startDayIndex = date.withDayOfMonth(1).dayOfWeekIndex
    val endDayIndex = startDayIndex + date.lengthOfMonth() - 1

    for (i in 0 until startDayIndex) {
        result[i] = previousMonthEndDay - (startDayIndex - i) + 1
    }
    for (i in startDayIndex..endDayIndex) {
        result[i] = i - startDayIndex + 1
    }
    for (i in (endDayIndex + 1)..41) {
        result[i] = i - endDayIndex
    }

    return Triple(result, startDayIndex, endDayIndex)
}

fun getWeekTexts(date: LocalDate): List<Int> {
    val (_result) = getMonthTexts(date)

    return _result.subList((date.weekOfMonth - 1) * 7, date.weekOfMonth * 7)
}