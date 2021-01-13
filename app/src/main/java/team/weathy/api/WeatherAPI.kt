package team.weathy.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import team.weathy.model.entity.DailyWeatherWithInDays
import team.weathy.model.entity.HourlyWeather
import team.weathy.model.entity.OverviewWeather
import team.weathy.model.entity.WeatherHumidity
import team.weathy.model.entity.WeatherRain
import team.weathy.model.entity.WeatherWind
import team.weathy.util.DateHourString
import team.weathy.util.DateOrDateHourString
import team.weathy.util.DateString


data class WeatherDailyHourlyRes(
    @SerializedName("overviewWeather") val weather: OverviewWeather?, val message: String
)

data class WithIn24HoursRes(
    @SerializedName("hourlyWeatherList") val list: List<HourlyWeather?>, val message: String,
)

data class WithIn7DaysRes(
    @SerializedName("dailyWeatherList") val list: List<DailyWeatherWithInDays?>, val message: String
)

data class WeatherDetailRes(
    val extraWeather: ExtraWeather?, val message: String
) {
    data class ExtraWeather(
        val rain: Rain, val humidity: Humidity, val wind: Wind
    ) {
        data class Rain(
            @SerializedName("value") val value: Int, @SerializedName("rating") val weatherRain: WeatherRain
        )

        data class Humidity(
            @SerializedName("value") val value: Int, @SerializedName("rating") val weatherHumidity: WeatherHumidity
        )

        data class Wind(
            @SerializedName("value") val value: Double, @SerializedName("rating") val weatherWind: WeatherWind
        )
    }
}


data class WeatherSearchRes(
    @SerializedName("overviewWeatherList") val list: List<OverviewWeather>?, val message: String
)

interface WeatherAPI {
    @GET("weather/overview")
    suspend fun fetchWeatherByLocation(
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("code") code: Long? = null,
        @Query("date") dateOrHourStr: DateOrDateHourString,
    ): Response<WeatherDailyHourlyRes>

    @GET("weather/forecast/hourly")
    suspend fun fetchWeatherWithIn24Hours(
        @Query("code") code: Long, @Query("date") dateHourStr: DateHourString
    ): WithIn24HoursRes

    @GET("weather/forecast/daily")
    suspend fun fetchWeatherWithIn7Days(
        @Query("code") code: Long, @Query("date") dateHourStr: DateString
    ): WithIn7DaysRes

    @GET("weather/daily/extra")
    suspend fun fetchWeatherDetail(
        @Query("code") code: Long, @Query("date") dateHourStr: DateString
    ): WeatherDetailRes

    @GET("weather/overviews")
    suspend fun searchWeather(
        @Query("keyword") keyword: String, @Query("date") dateOrHourStr: DateOrDateHourString
    ): WeatherSearchRes

}