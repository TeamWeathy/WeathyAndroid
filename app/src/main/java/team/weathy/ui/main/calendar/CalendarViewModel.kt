package team.weathy.ui.main.calendar

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import team.weathy.api.WeathyAPI
import team.weathy.di.ApiMock
import team.weathy.model.entity.CalendarPreview
import team.weathy.model.entity.Weathy
import team.weathy.util.extensions.launchCatch
import java.time.LocalDate

class CalendarViewModel @ViewModelInject constructor(@ApiMock private val weathyAPI: WeathyAPI) : ViewModel() {
    val curDate = MutableLiveData(LocalDate.now())

    private val _calendarData = MutableLiveData<Map<String, List<CalendarPreview?>>>(mapOf())
    val calendarData: LiveData<Map<String, List<CalendarPreview?>>> = _calendarData

    private val _curWeathy = MutableLiveData<Weathy?>(null)
    val curWeathy: LiveData<Weathy?> = _curWeathy

    private val _isMoreMenuShowing = MutableLiveData(false)
    val isMoreMenuShowing: LiveData<Boolean> = _isMoreMenuShowing

    fun onClickMoreMenu() {
        _isMoreMenuShowing.value = !isMoreMenuShowing.value!!
    }

    fun onClickContainer() {
        if (isMoreMenuShowing.value == true) _isMoreMenuShowing.value = false
    }

    fun onCurDateChanged() {
        launchCatch({
            weathyAPI.fetchWeathyWithId(1)
        }, onSuccess = {
            _curWeathy.value = it.weathy
        })
    }
}