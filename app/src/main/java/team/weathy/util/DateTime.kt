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
    val subtracted = cur.minusWeeks(diffWeek.toLong())

    return subtracted

    //    return if (subtracted.lengthOfMonth() - subtracted.dayOfMonth < 6) {
    //        subtracted.plusMonths(1).withDayOfMonth(1)
    //    } else {
    //        subtracted
    //    }
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

fun getMonthTexts(date: LocalDate): Triple<List<Int>, Int, Int> {
    val result = MutableList(42) { 0 }

    val previousMonthEndDay = date.minusMonths(1).lengthOfMonth()

    // 1 ~ 7 (MON ~ SUN)
    val startDayIndex = transformDayOfWeek(date.withDayOfMonth(1).dayOfWeek) - 1
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

    return _result.subList(date.weekOfMonth * 7, date.weekOfMonth * 7 + 7)
}