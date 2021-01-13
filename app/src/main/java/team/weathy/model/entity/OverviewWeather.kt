package team.weathy.model.entity

import com.google.gson.annotations.SerializedName

data class OverviewWeather(
    val region: Region,
    @SerializedName("dailyWeather") val daily: DailyWeather,
    @SerializedName("hourlyWeather") val hourly: HourlyWeather,
)