package team.weathy.util

import team.weathy.view.calendar.MonthlyAdapter
import team.weathy.view.calendar.WeeklyAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*


val LocalDate.weekOfMonth: Int
    get() {
        val gc = GregorianCalendar.from(atStartOfDay(ZoneId.systemDefault()))
        return gc[Calendar.WEEK_OF_MONTH]
    }

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

    val diffIndex = ChronoUnit.WEEKS.between(date, now).toInt()

    return WeeklyAdapter.MAX_ITEM_COUNT - diffIndex - 1
}

fun transformDayOfWeek(dayOfWeek: DayOfWeek): Int {
    return when (dayOfWeek.value + 1) {
        8 -> 1
        else -> dayOfWeek.value + 1
    }
}

fun calculateRequiredRow(date: LocalDate): Int {
    val dayCount = date.lengthOfMonth()
    val firstWeekStartDayOfWeek = transformDayOfWeek(date.withDayOfMonth(1).dayOfWeek)
    val previousMonthDayCount = firstWeekStartDayOfWeek - 1

    return (dayCount + previousMonthDayCount) / 7 + 1
}

fun getMonthTexts(date: LocalDate): List<String> {
    val result = MutableList(42) { "" }

    date.lengthOfMonth()

    // 1 ~ 7 (MON ~ SUN)
    var startDay = transformDayOfWeek(date.withDayOfMonth(1).dayOfWeek)
    when (startDay) {
        8 -> {
            startDay = 1
        }
    }
    for (i in 1..date.lengthOfMonth()) {
        result[startDay + i - 2] = i.toString()
    }

    return result
}

fun getWeekTexts(date: LocalDate): List<String> {
    return listOf()
}