package team.weathy.ui.main.calendar

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import team.weathy.api.CalendarAPI
import team.weathy.api.WeathyAPI
import team.weathy.di.ApiMock
import team.weathy.model.entity.CalendarPreview
import team.weathy.model.entity.Weathy
import team.weathy.util.dateString
import team.weathy.util.debugE
import team.weathy.util.extensions.launchCatch
import team.weathy.util.extensions.updateMap
import team.weathy.util.getEndDateStringInCalendar
import team.weathy.util.getStartDateStringInCalendar
import team.weathy.util.yearMonthFormat
import java.time.LocalDate

typealias YearMonthFormat = String

class CalendarViewModel @ViewModelInject constructor(
    @ApiMock private val calendarAPI: CalendarAPI, @ApiMock private val weathyAPI: WeathyAPI
) : ViewModel() {

    private val curDateFlow = MutableStateFlow(LocalDate.now())
    private val _curDateMutable = MutableLiveData(LocalDate.now())
    val curDate: LiveData<LocalDate> = _curDateMutable

    private val yearMonthFormatFlow = curDateFlow.map {
        it.yearMonthFormat
    }.distinctUntilChanged()

    private val _calendarData = MutableLiveData<Map<YearMonthFormat, List<CalendarPreview?>>>(mapOf())
    val calendarData: LiveData<Map<YearMonthFormat, List<CalendarPreview?>>> = _calendarData

    private val _curWeathy = MutableLiveData<Weathy?>(null)
    val curWeathy: LiveData<Weathy?> = _curWeathy

    private val _isMoreMenuShowing = MutableLiveData(false)
    val isMoreMenuShowing: LiveData<Boolean> = _isMoreMenuShowing

    init {
        collectCurDateFlow()
        collectYearMonthFlow()
    }

    fun onClickMoreMenu() {
        _isMoreMenuShowing.value = !isMoreMenuShowing.value!!
    }

    fun onClickContainer() {
        if (isMoreMenuShowing.value == true) _isMoreMenuShowing.value = false
    }

    fun onCurDateChanged(date: LocalDate) {
        if (curDateFlow.value.year == date.year && curDateFlow.value.monthValue == date.monthValue && curDateFlow.value.dayOfMonth == date.dayOfMonth) {
            return
        }
        curDateFlow.value = date
    }

    private fun collectCurDateFlow() {
        viewModelScope.launch {
            curDateFlow.collect {
                _curDateMutable.value = it
                fetchCurDateWeathy()
            }
        }
    }

    private fun fetchCurDateWeathy() {
        debugE("fetchCurDateWeathy ${curDateFlow.value.dateString}")
        launchCatch({
            weathyAPI.fetchWeathyWithDate(curDateFlow.value.dateString)
        }, onSuccess = {
            _curWeathy.value = it.weathy
        })
    }

    private fun collectYearMonthFlow() = viewModelScope.launch {
        yearMonthFormatFlow.collect {
            fetchMonthlyDataIfNeeded()
        }
    }

    private fun fetchMonthlyDataIfNeeded() {
        debugE("fetchMonthlyDataIfNeeded ${curDateFlow.value.dateString}")
        val date = curDateFlow.value
        if (!_calendarData.value!!.containsKey(date.yearMonthFormat)) {
            launchCatch({
                val s = getStartDateStringInCalendar(date.year, date.monthValue)
                val e = getEndDateStringInCalendar(date.year, date.monthValue)
                calendarAPI.fetchCalendarPreview(s, e)
            }, onSuccess = { (list) ->
                _calendarData.updateMap {
                    this[date.yearMonthFormat] = list
                }
            })
        }
    }
}