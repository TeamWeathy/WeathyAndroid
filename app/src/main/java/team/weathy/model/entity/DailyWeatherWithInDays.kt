package team.weathy.model.entity

data class DailyWeatherWithInDays(
    val time: String, val temperature: Temperature, val climate: Climate
)