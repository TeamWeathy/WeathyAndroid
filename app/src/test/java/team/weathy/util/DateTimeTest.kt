package team.weathy.util

import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DateTimeTest {
    @Test
    fun test() {
        val date1 = LocalDate.of(2020, 1, 20)
        val date2 = LocalDate.of(2020, 1, 14)

        println(ChronoUnit.WEEKS.between(date1, date2).toInt())
    }
}