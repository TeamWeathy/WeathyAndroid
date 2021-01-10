package team.weathy.ui.main.calendar

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import team.weathy.api.CalendarAPI
import team.weathy.api.WeathyAPI
import team.weathy.di.ApiMock
import team.weathy.model.entity.CalendarPreview
import team.weathy.model.entity.Weather
import team.weathy.model.entity.Weathy
import team.weathy.util.dateString
import team.weathy.util.debugE
import team.weathy.util.extensions.launchCatch
import team.weathy.util.extensions.updateMap
import team.weathy.util.getEndDateStringInCalendar
import team.weathy.util.getStartDateStringInCalendar
import team.weathy.util.koFormat
import team.weathy.util.yearMonthFormat
import java.time.LocalDate

typealias YearMonthFormat = String

class CalendarViewModel @ViewModelInject constructor(
    @ApiMock private val calendarAPI: CalendarAPI, @ApiMock private val weathyAPI: WeathyAPI
) : ViewModel() {

    private val _curDate = MutableLiveData(LocalDate.now())
    val curDate: LiveData<LocalDate> = _curDate

    private val _calendarData = MutableLiveData<Map<YearMonthFormat, List<CalendarPreview?>>>(mapOf())
    val calendarData: LiveData<Map<YearMonthFormat, List<CalendarPreview?>>> = _calendarData

    private val _isMoreMenuShowing = MutableLiveData(false)
    val isMoreMenuShowing: LiveData<Boolean> = _isMoreMenuShowing

    private val _curWeathy = MutableLiveData<Weathy?>(null)
    val curWeathy: LiveData<Weathy?> = _curWeathy
    val weathyDate = curDate.map {
        it.koFormat
    }
    val weathyLocation = curWeathy.map {
        it?.dailyWeather?.region?.name ?: ""
    }
    val weathyWeatherIcon = curWeathy.map {
        Weather.withId(it?.hourlyWeather?.climate?.iconId).iconId
    }
    val weathyClimateDescription = curWeathy.map {
        it?.hourlyWeather?.climate?.description ?: ""
    }
    val weathyTempHigh = curWeathy.map {
        it?.dailyWeather?.temperature?.maxTemp?.toString()?.plus("°") ?: "0°"
    }
    val weathyTempLow = curWeathy.map {
        it?.dailyWeather?.temperature?.minTemp?.toString()?.plus("°") ?: "0°"
    }
    val weathyStampIcon = curWeathy.map {
        it?.stampId?.iconRes
    }
    val weathyStampRepresentation = curWeathy.map {
        it?.stampId?.representationPast
    }
    val weathyFeedback = curWeathy.map {
        it?.feedback ?: ""
    }

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
        if (_curDate.value!!.year == date.year && _curDate.value!!.monthValue == date.monthValue && _curDate.value!!.dayOfMonth == date.dayOfMonth) {
            return
        }
        _curDate.value = date
    }

    private fun collectCurDateFlow() {
        viewModelScope.launch {
            _curDate.asFlow().collect {
                fetchCurDateWeathy()
            }
        }
    }

    private fun fetchCurDateWeathy() {
        debugE("fetchCurDateWeathy ${_curDate.value!!.dateString}")
        launchCatch({
            weathyAPI.fetchWeathyWithDate(_curDate.value!!.dateString)
        }, onSuccess = {
            _curWeathy.value = it.weathy
        })
    }

    private fun collectYearMonthFlow() = viewModelScope.launch {
        _curDate.asFlow().map { it.yearMonthFormat }.distinctUntilChanged().collect {
            fetchMonthlyDataIfNeeded()
        }
    }

    private fun fetchMonthlyDataIfNeeded() {
        debugE("fetchMonthlyDataIfNeeded ${_curDate.value!!.dateString}")
        val date = _curDate.value!!
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