package team.weathy.api.mock

import team.weathy.api.WeatherAPI
import team.weathy.api.WeatherDailyHourlyRes
import team.weathy.api.WeatherDetailRes
import team.weathy.api.WeatherDetailRes.ExtraWeather
import team.weathy.api.WeatherDetailRes.ExtraWeather.Humidity
import team.weathy.api.WeatherDetailRes.ExtraWeather.Rain
import team.weathy.api.WeatherDetailRes.ExtraWeather.Wind
import team.weathy.api.WeatherSearchRes
import team.weathy.api.WithIn24HoursRes
import team.weathy.api.WithIn7DaysRes
import team.weathy.model.entity.OverviewWeather
import team.weathy.model.entity.WeatherHumidity
import team.weathy.model.entity.WeatherRain
import team.weathy.model.entity.WeatherWind
import team.weathy.util.DateHourString
import team.weathy.util.DateOrDateHourString
import team.weathy.util.DateString
import javax.inject.Inject

class MockWeatherAPI @Inject constructor() : WeatherAPI {
    override suspend fun fetchWeatherByLocation(
        lat: Double?, lon: Double?, code: Long?, dateOrHourStr: DateOrDateHourString
    ): WeatherDailyHourlyRes {
        return WeatherDailyHourlyRes(
            OverviewWeather(
                MockGenerator.region(), MockGenerator.dailyWeather(), MockGenerator.hourlyWeather()
            ), "message"
        )
    }

    override suspend fun fetchWeatherWithIn24Hours(code: Long, dateHourStr: DateHourString): WithIn24HoursRes {
        return WithIn24HoursRes(listOf(MockGenerator.hourlyWeather()), "")
    }

    override suspend fun fetchWeatherWithIn7Days(code: Long, dateHourStr: DateString): WithIn7DaysRes {
        return WithIn7DaysRes(listOf(), "")
    }

    override suspend fun fetchWeatherDetail(code: Long, dateHourStr: DateString): WeatherDetailRes {
        return WeatherDetailRes(
            ExtraWeather(
                Rain(1, WeatherRain.values().random()),
                Humidity(1, WeatherHumidity.values().random()),
                Wind(1.0, WeatherWind.values().random())
            ), ""
        )
    }

    override suspend fun searchWeather(keyword: String, dateOrHourStr: DateOrDateHourString): WeatherSearchRes {
        return WeatherSearchRes(
            listOf(
                OverviewWeather(
                    MockGenerator.region(), MockGenerator.dailyWeather(), MockGenerator.hourlyWeather()
                ),
            ), "success"
        )
    }
}