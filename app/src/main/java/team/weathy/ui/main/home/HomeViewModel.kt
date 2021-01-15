package team.weathy.ui.main.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import team.weathy.api.WeatherAPI
import team.weathy.api.WeatherDetailRes.ExtraWeather
import team.weathy.api.WeathyAPI
import team.weathy.di.Api
import team.weathy.model.entity.DailyWeatherWithInDays
import team.weathy.model.entity.HourlyWeather
import team.weathy.model.entity.OverviewWeather
import team.weathy.model.entity.Weathy
import team.weathy.util.SPUtil
import team.weathy.util.dateHourString
import team.weathy.util.dateString
import team.weathy.util.debugE
import team.weathy.util.koFormat
import team.weathy.util.location.LocationUtil
import java.time.LocalDate
import java.time.LocalDateTime

@FlowPreview
class HomeViewModel @ViewModelInject constructor(
    private val locationUtil: LocationUtil,
    @Api private val weatherAPI: WeatherAPI,
    @Api private val weathyAPI: WeathyAPI,
    private val spUtil: SPUtil,
) : ViewModel() {
    private val lastFetchDateTime = MutableLiveData(LocalDateTime.now())

    val currentWeather = locationUtil.selectedWeatherLocation.asLiveData(viewModelScope.coroutineContext)
    val loadingWeather = MutableLiveData(true)

    val isOtherPlaceSelected = locationUtil.isOtherPlaceSelected.asLiveData(viewModelScope.coroutineContext)

    val recommendedWeathy = MutableLiveData<Weathy?>(null)
    val loadingRecommendedWeathy = MutableLiveData(true)

    val hourlyWeathers = MutableLiveData<List<HourlyWeather>>(listOf())
    val dailyWeathers = MutableLiveData<List<DailyWeatherWithInDays>>(listOf())
    val extraWeathers = MutableLiveData<ExtraWeather>()

    // region UI
    val weatherFirstBackground = currentWeather.map {
        it?.hourly?.climate?.weather?.firstHomeBackgroundId
    }
    val weatherSecondBackground = currentWeather.map {
        it?.hourly?.climate?.weather?.secondHomeBackgroundId
    }
    val weatherIcon = currentWeather.map {
        it?.hourly?.climate?.weather?.bigIconId
    }

    val datetimeText = lastFetchDateTime.map { it.koFormat }
    val regionText = currentWeather.map { it?.region?.name ?: "" }
    val curTempText = currentWeather.map { "${it?.hourly?.temperature ?: 0}°" }
    val maxTempText = currentWeather.map { "${it?.daily?.temperature?.maxTemp ?: 0}°" }
    val minTempText = currentWeather.map { "${it?.daily?.temperature?.minTemp ?: 0}°" }
    val descriptionText = currentWeather.map { it?.hourly?.climate?.description ?: "" }

    val weathyDate = recommendedWeathy.map {
        it ?: return@map ""
        val (year, month, day) = it.dailyWeather.date
        "${year}년 ${month}월 ${day}일"
    }
    val weathyWeatherIcon = recommendedWeathy.map {
        it?.hourlyWeather?.climate?.weather?.smallIconId
    }
    val weathyClimateDescription = recommendedWeathy.map {
        it?.hourlyWeather?.climate?.description ?: ""
    }
    val weathyTempHigh = recommendedWeathy.map {
        it?.dailyWeather?.temperature?.maxTemp?.toString()?.plus("°") ?: "0°"
    }
    val weathyTempLow = recommendedWeathy.map {
        it?.dailyWeather?.temperature?.minTemp?.toString()?.plus("°") ?: "0°"
    }
    val weathyStampIcon = recommendedWeathy.map {
        it?.stampId?.iconRes
    }
    val weathyStampColor = recommendedWeathy.map {
        it?.stampId?.colorRes
    }
    val weathyStampRepresentation = recommendedWeathy.map {
        it?.stampId?.representationPast
    }
    val weathyTopClothes = recommendedWeathy.map {
        it?.closet?.top?.clothes?.joinToString(" • ") { it.name } ?: ""
    }
    val weathyBottomClothes = recommendedWeathy.map {
        it?.closet?.bottom?.clothes?.joinToString(" • ") { it.name } ?: ""
    }
    val weathyOuterClothes = recommendedWeathy.map {
        it?.closet?.outer?.clothes?.joinToString(" • ") { it.name } ?: ""
    }
    val weathyEtcClothes = recommendedWeathy.map {
        it?.closet?.etc?.clothes?.joinToString(" • ") { it.name } ?: ""
    }


    val extraRainRepresentation = extraWeathers.map {
        it ?: return@map ""
        it.rain.weatherRain.representation
    }
    val extraRainValue = extraWeathers.map {
        it ?: return@map ""
        "${it.rain.value}mm"
    }

    val extraHumidityRepresentation = extraWeathers.map {
        it ?: return@map ""
        it.humidity.weatherHumidity.representation
    }
    val extraHumidityValue = extraWeathers.map {
        it ?: return@map ""
        "${it.humidity.value}%"
    }

    val extraWindRepresentation = extraWeathers.map {
        it ?: return@map ""
        it.wind.weatherWind.representation
    }
    val extraWindValue = extraWeathers.map {
        it ?: return@map ""
        "${it.wind.value}m/s"
    }
    // endregion

    init {
        viewModelScope.launch {
            collectCurrentWeatherAndFetch()
        }

        viewModelScope.launch {
            if (spUtil.lastSelectedLocationCode != 0L) {
                fetchWeatherWithCode(spUtil.lastSelectedLocationCode)?.let {
                    if (locationUtil.isOtherPlaceSelected.value) {
                        locationUtil.selectOtherPlace(it)
                    } else {
                        locationUtil.selectPlace(it)
                    }
                }
            }
            fetchWeatherAfterLocationAvailable()
        }
    }

    private suspend fun fetchWeatherAfterLocationAvailable() {
        val result = locationUtil.lastLocation.combine(locationUtil.isOtherPlaceSelected) { a, b -> a to b }
            .filter { (lastLocation, isOtherPlaceSelected) ->
                !isOtherPlaceSelected && lastLocation != null
            }.first()
        val lastLocation = result.first!!

        loadingWeather.value = true
        kotlin.runCatching {
            lastFetchDateTime.value = LocalDateTime.now()

            weatherAPI.fetchWeatherByLocation(
                lastLocation.latitude, lastLocation.longitude, dateOrHourStr = lastFetchDateTime.value!!.dateHourString
            )
        }.onSuccess { res ->
            val weather = res.body()?.weather ?: return@onSuccess
            locationUtil.selectPlace(weather)
        }.onFailure {

        }
        loadingWeather.value = false
    }

    private suspend fun fetchWeatherWithCode(code: Long): OverviewWeather? {
        return weatherAPI.fetchWeatherByLocation(code = code, dateOrHourStr = LocalDateTime.now().dateHourString)
            .body()?.weather
    }

    private suspend fun collectCurrentWeatherAndFetch() {
        currentWeather.asFlow().collect { weather ->
            weather ?: return@collect
            val code = weather.region.code
            val dateString = LocalDate.now().dateString
            val dateHourString = LocalDateTime.now().dateHourString

            loadingRecommendedWeathy.value = true
            kotlin.runCatching {
                weathyAPI.fetchRecommendedWeathy(
                    code, dateString
                )
            }.onSuccess { res ->
                recommendedWeathy.value = res.body()?.weathy
            }.onFailure {
                debugE(it)
            }
            loadingRecommendedWeathy.value = false

            kotlin.runCatching {
                weatherAPI.fetchWeatherWithIn24Hours(code, dateHourString)
            }.onSuccess {
                hourlyWeathers.value = it.list.filterNotNull()
            }

            kotlin.runCatching {
                weatherAPI.fetchWeatherWithIn7Days(code, dateHourString)
            }.onSuccess {
                dailyWeathers.value = it.list.filterNotNull()
            }

            kotlin.runCatching {
                weatherAPI.fetchWeatherDetail(code, dateHourString)
            }.onSuccess {
                extraWeathers.value = it.extraWeather
            }
        }
    }
}