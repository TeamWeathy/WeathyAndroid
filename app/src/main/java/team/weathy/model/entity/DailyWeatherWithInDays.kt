package team.weathy.model.entity

import com.google.gson.annotations.SerializedName


data class DailyWeatherWithInDays(
    val date: Date, val temperature: Temperature, val climate: Climate
) {
    data class Date(
        val year: Int, val month: Int, val day: Int, val dayOfWeek: String
    )

    data class Climate(
        @SerializedName("iconId") val weather: Weather
    )
}