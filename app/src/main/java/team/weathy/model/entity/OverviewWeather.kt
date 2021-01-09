package team.weathy.model.entity

import com.google.gson.annotations.SerializedName

data class OverviewWeather(
    @SerializedName("dailyWeather") val daily: DailyWeather,
    @SerializedName("hourlyWeather") val hourly: HourlyWeather,
)