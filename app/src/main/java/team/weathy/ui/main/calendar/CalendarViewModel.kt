package team.weathy.ui.main.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import team.weathy.util.DateTime

class CalendarViewModel : ViewModel() {
    private val _curWeek = MutableLiveData(DateTime.now())
    val curWeek: LiveData<DateTime> = _curWeek
}