package team.weathy.model.entity

data class Weathy(
    val dailyWeather: DailyWeather,
    val hourlyWeather: HourlyWeather,
    val closet: WeathyCloset,
    val stampId: Int,
    val feedback: String
)