package team.weathy.model.entity

import com.google.gson.annotations.SerializedName

data class Weathy(
    @SerializedName("weathyId") val id: Int,
    val region: Region,
    val dailyWeather: DailyWeather,
    val hourlyWeather: HourlyWeather,
    val closet: WeathyCloset,
    val stampId: WeatherStamp,
    val feedback: String
)