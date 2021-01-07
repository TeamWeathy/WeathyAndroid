package team.weathy.model.entity

data class DailyWeather(
    val region: Region, val date: Date, val temperature: Temperature
)