package team.weathy.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DateTimeTest {
    @Test
    fun test() {
        val date1 = LocalDate.of(2020, 1, 20)
        val date2 = LocalDate.of(2020, 1, 14)

        assertThat(date2.until(date1, ChronoUnit.WEEKS)).isEqualTo(0)
    }
}