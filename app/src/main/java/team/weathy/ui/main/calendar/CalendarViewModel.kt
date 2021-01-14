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
import team.weathy.di.Api
import team.weathy.model.entity.CalendarPreview
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
    @Api private val calendarAPI: CalendarAPI, @Api private val weathyAPI: WeathyAPI
) : ViewModel() {

    private val _curDate = MutableLiveData(LocalDate.now())
    val curDate: LiveData<LocalDate> = _curDate

    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _calendarData = MutableLiveData<Map<YearMonthFormat, List<CalendarPreview?>>>(mapOf())
    val calendarData: LiveData<Map<YearMonthFormat, List<CalendarPreview?>>> = _calendarData

    private val _isMoreMenuShowing = MutableLiveData(false)
    val isMoreMenuShowing: LiveData<Boolean> = _isMoreMenuShowing

    private val selectedWeathy = MutableLiveData<Weathy?>(null)
    val curWeathy: LiveData<Weathy?> = selectedWeathy
    val weathyDate = selectedDate.map {
        it.koFormat
    }
    val weathyLocation = curWeathy.map {
        it?.region?.name ?: ""
    }
    val weathyWeatherIcon = curWeathy.map {
        it?.hourlyWeather?.climate?.weather?.smallIconId
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
    val weathyTopClothes = curWeathy.map {
        it?.closet?.top?.clothes?.joinToString(" . ") { it.name } ?: ""
    }
    val weathyBottomClothes = curWeathy.map {
        it?.closet?.bottom?.clothes?.joinToString(" . ") { it.name } ?: ""
    }
    val weathyOuterClothes = curWeathy.map {
        it?.closet?.outer?.clothes?.joinToString(" . ") { it.name } ?: ""
    }
    val weathyEtcClothes = curWeathy.map {
        it?.closet?.etc?.clothes?.joinToString(" . ") { it.name } ?: ""
    }
    val weathyFeedback = curWeathy.map {
        it?.feedback ?: ""
    }

    init {
        collectSelectedFlow()
        collectYearMonthFlow()
    }

    fun onClickMoreMenu() {
        _isMoreMenuShowing.value = !isMoreMenuShowing.value!!
    }

    fun onClickContainer() {
        if (isMoreMenuShowing.value == true) _isMoreMenuShowing.value = false
    }

    fun onCurDateChanged(date: LocalDate) {
        _curDate.value = date
    }

    fun onSelectedDateChanged(date: LocalDate) {
        _selectedDate.value = date
    }

    private fun collectSelectedFlow() {
        viewModelScope.launch {
            selectedDate.asFlow().collect {
                fetchSelectedDateWeathy()
            }
        }
    }

    private fun fetchSelectedDateWeathy() {
        debugE("fetchSelectedDateWeathy ${selectedDate.value!!.dateString}")
        launchCatch({
            weathyAPI.fetchWeathyWithDate(selectedDate.value!!.dateString)
        }, onSuccess = {
            selectedWeathy.value = it.weathy
        }, onFailure = {
            selectedWeathy.value = null
        })
    }

    private fun collectYearMonthFlow() = viewModelScope.launch {
        curDate.asFlow().map { it.yearMonthFormat }.distinctUntilChanged().collect {
            fetchMonthlyDataIfNeeded()
        }
    }

    private fun fetchMonthlyDataIfNeeded() {
        debugE("fetchMonthlyDataIfNeeded ${curDate.value!!.dateString}")
        val date = curDate.value!!
        if (!calendarData.value!!.containsKey(date.yearMonthFormat)) {
            launchCatch({
                val s = getStartDateStringInCalendar(date.year, date.monthValue)
                val e = getEndDateStringInCalendar(date.year, date.monthValue)
                calendarAPI.fetchCalendarPreview(s, e)
            }, onSuccess = { (list) ->
                _calendarData.updateMap {
                    this[date.yearMonthFormat] = list
                }
                debugE(calendarData.value!!)
            })
        }
    }
}