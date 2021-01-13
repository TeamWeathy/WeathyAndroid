package team.weathy.model.entity

import com.google.gson.annotations.SerializedName

data class HourlyWeather(
    val time: String?, val temperature: Int?, val climate: Climate, val pop: Int
) {
    data class Climate(
        @SerializedName("iconId") val weather: Weather, @SerializedName("description") val description: String
    )
}