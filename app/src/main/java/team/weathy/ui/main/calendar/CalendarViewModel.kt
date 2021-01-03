package team.weathy.ui.main.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import team.weathy.view.calendar.CalendarView.CalendarDate

class CalendarViewModel : ViewModel() {
    val curDate = MutableLiveData(CalendarDate.now())
}