package team.weathy.ui.main.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    val curDate = MutableLiveData(LocalDate.now())
}