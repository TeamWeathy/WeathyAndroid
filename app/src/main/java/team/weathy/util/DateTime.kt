package team.weathy.util

import team.weathy.view.calendar.MonthlyAdapter
import team.weathy.view.calendar.WeeklyAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*

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

fun String.padZero(length: Int = 2) = padStart(length, '0')
fun Int.padZero() = toString().padZero()


/**
 * 1 -> 월
 * 7 -> 일
 */
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

/**
 * 1 -> 오전 1시
 * 13 -> 오후 1시
 * 24 -> 오후 12시
 */
val Int.koFormat: String
    get() = if (this < 13) {
        "오전 ${this}시"
    } else {
        "오후 ${this - 12}시"
    }

/**
 * 1997-04
 */
val LocalDate.yearMonthFormat: String
    get() = "${year}-${monthValue.padZero()}"

/**
 * 0 ~ 6
 */
val LocalDate.weekOfMonth: Int
    get() {
        // Or use a specific locale, or configure your own rules
        val weekFields: WeekFields = WeekFields.of(Locale.KOREA)
        return get(weekFields.weekOfMonth())

        //        val gc = GregorianCalendar.from(atStartOfDay(ZoneId.systemDefault()))
        //        gc.firstDayOfWeek = SUNDAY
        //        gc.minimalDaysInFirstWeek = 1
        //        return gc[WEEK_OF_MONTH]
    }

/**
 * 1(SUN) -> 7(SAT)
 */
val LocalDate.dayOfWeekValue: Int
    get() = when (dayOfWeek.value + 1) {
        8 -> 1
        else -> dayOfWeek.value + 1
    }

/** 0(SUN) -> 6(SAT) */
val LocalDate.dayOfWeekIndex: Int
    get() = dayOfWeekValue - 1

val LocalDate.dateString: DateString
    get() = this.format(DateTimeFormatter.ISO_LOCAL_DATE)

fun LocalDate.isSameDay(date: LocalDate): Boolean {
    return year == date.year && month == date.month && dayOfMonth == date.dayOfMonth
}

fun LocalDate.isFuture(): Boolean {
    return this.isAfter(LocalDate.now())
}

fun LocalDate.isPast(): Boolean {
    return isBefore(LocalDate.of(2020, 1, 12))
}

fun LocalDate.isAvailable(): Boolean {
    return !isFuture() && !isPast()
}

/** 1997-04-04T23 */
val LocalDateTime.dateHourString: DateHourString
    get() = "${year}-${monthValue.padZero()}-${dayOfMonth.padZero()}T${hour.padZero()}"

/** 1997-04-04 */
val LocalDateTime.dateString: DateString
    get() = "${year}-${monthValue.padZero()}-${dayOfMonth.padZero()}"

/**
 * 4월 4일 월요일 · 오전 1시
 */
val LocalDateTime.koFormat: String
    get() = "${monthValue}월 ${dayOfMonth}일 ${dayOfWeek.koFormat}요일 · ${hour.koFormat}"

/** 4월 4일 금요일 */
val LocalDate.koFormat: String
    get() = "${monthValue}월 ${dayOfMonth}일 ${dayOfWeek.koFormat}요일"

/** 4월 4일*/
val LocalDate.monthDayFormat: String
    get() = "${monthValue}월 ${dayOfMonth}일"

fun convertMonthlyIndexToDateToFirstDateOfMonthCalendar(index: Int): Pair<LocalDate, LocalDate> {
    val cur = LocalDate.now()

    val diffMonth = MonthlyAdapter.MAX_ITEM_COUNT - index - 1
    val monthSubtracted = cur.minusMonths(diffMonth.toLong())
    val firstDateOfMonth = monthSubtracted.withDayOfMonth(1)
    val startIdx = firstDateOfMonth.dayOfWeekIndex

    return firstDateOfMonth.minusDays(startIdx.toLong()) to firstDateOfMonth
}

fun convertWeeklyIndexToFirstDateOfWeekCalendar(index: Int): LocalDate {
    val cur = LocalDate.now()

    val diffWeek = WeeklyAdapter.MAX_ITEM_COUNT - index - 1
    val weekSubtracted = cur.minusWeeks(diffWeek.toLong())
    val startIdx = weekSubtracted.dayOfWeekIndex

    return weekSubtracted.minusDays(startIdx.toLong())
}

fun convertDateToMonthlyIndex(date: LocalDate): Int {
    val now = LocalDate.now()

    val yearDiff = now.year - date.year
    val diffIndex = now.monthValue - date.monthValue + yearDiff * 12

    return MonthlyAdapter.MAX_ITEM_COUNT - diffIndex - 1
}

fun convertDateToWeeklyIndex(date: LocalDate): Int {
    val now = LocalDate.now()
    val nowDayOfWeekIndex = now.dayOfWeekIndex
    val nowFirstDayOfWeek = now.minusDays(nowDayOfWeekIndex.toLong())

    val dateDayOfWeekIndex = date.dayOfWeekIndex
    val dateFirstDayOfWeek = date.minusDays(dateDayOfWeekIndex.toLong())

    val weekDiff = dateFirstDayOfWeek.until(nowFirstDayOfWeek, ChronoUnit.WEEKS).toInt()

    return WeeklyAdapter.MAX_ITEM_COUNT - weekDiff - 1
}


fun calculateRequiredRow(date: LocalDate): Int {
    return (date.lengthOfMonth() + date.withDayOfMonth(1).dayOfWeekIndex - 1) / 7 + 1
}

fun getStartDateStringInCalendar(year: Int, month: Int): DateString {
    val date = LocalDate.of(year, month, 1)
    val startDayIndex = date.dayOfWeekIndex

    val startDateInCalendar = date.minusDays(startDayIndex.toLong())
    return startDateInCalendar.dateString
}

fun getEndDateStringInCalendar(year: Int, month: Int): DateString {
    val firstDate = LocalDate.of(year, month, 1)
    val lastDate = LocalDate.of(year, month, firstDate.lengthOfMonth())
    val endDayIndex = lastDate.dayOfWeekIndex

    val endDateInCalendar = lastDate.plusDays(6 - endDayIndex.toLong())
    return endDateInCalendar.dateString
}