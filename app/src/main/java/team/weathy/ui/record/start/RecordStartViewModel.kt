package team.weathy.ui.record.start

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import team.weathy.api.WeatherAPI
import team.weathy.di.Api
import team.weathy.model.entity.OverviewWeather
import team.weathy.util.dateString
import team.weathy.util.koFormat
import team.weathy.util.location.LocationUtil
import team.weathy.util.monthDayFormat
import team.weathy.util.yearMonthFormat
import java.time.LocalDate

class RecordStartViewModel @ViewModelInject constructor(
	private val locationUtil: LocationUtil,
	@Api private val weatherAPI: WeatherAPI,
) : ViewModel() {
	val lastFetchDate = MutableLiveData(LocalDate.now())

	val currentWeather = locationUtil.selectedWeatherLocation.asLiveData(viewModelScope.coroutineContext)
	val loadingWeather = MutableLiveData(true)

	val dateTitle = lastFetchDate.map { it.monthDayFormat }
	val dateText = lastFetchDate.map { it.koFormat }
	val regionText = currentWeather.map { it?.region?.name ?: "" }
	val maxTempText = currentWeather.map { "${it?.daily?.temperature?.maxTemp ?: 0}°" }
	val minTempText = currentWeather.map { "${it?.daily?.temperature?.minTemp ?: 0}°" }

	init {
		collectLastLocationForFetchByLocation()
	}

	private fun collectLastLocationForFetchByLocation() = viewModelScope.launch {
		locationUtil.lastLocation.collect {
			it ?: return@collect

			loadingWeather.value = true
			kotlin.runCatching {
				lastFetchDate.value = LocalDate.now()

				weatherAPI.fetchWeatherByLocation(
					it.latitude, it.longitude, dateOrHourStr = lastFetchDate.value!!.dateString
				)
			}.onSuccess { res ->
//				res.weather ?: return@onSuccess
//				locationUtil.selectPlace(res.weather)
			}.onFailure {

			}
			loadingWeather.value = false
		}
	}

	fun onLocationChanged(weather: OverviewWeather) {
		locationUtil.selectOtherPlace(weather)
	}
}