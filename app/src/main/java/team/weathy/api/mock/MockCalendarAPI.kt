package team.weathy.api.mock

import team.weathy.api.CalendarAPI
import team.weathy.api.FetchCalendarPreviewRes
import team.weathy.util.DateString
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class MockCalendarAPI @Inject constructor() : CalendarAPI {
    override suspend fun fetchCalendarPreview(start: DateString, end: DateString): FetchCalendarPreviewRes {
        val startYear = start.substring(0, 4).toInt()
        val startMonth = start.substring(5, 7).toInt()
        val startDayOfMonth = start.substring(8, 10).toInt()

        val endYear = end.substring(0, 4).toInt()
        val endMonth = end.substring(5, 7).toInt()
        val endDayOfMonth = end.substring(8, 10).toInt()

        val startDate = LocalDate.of(startYear, startMonth, startDayOfMonth)
        val endDate = LocalDate.of(endYear, endMonth, endDayOfMonth)

        val dataCount = startDate.until(endDate, ChronoUnit.DAYS).toInt() + 1

        return MockGenerator.calendarPreviews(dataCount)
    }
}