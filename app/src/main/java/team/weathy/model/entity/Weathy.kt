package team.weathy.model.entity

data class Weathy(
    val region: Region,
    val dailyWeather: DailyWeather,
    val hourlyWeather: HourlyWeather,
    val closet: WeathyCloset,
    val stampId: WeatherStamp,
    val feedback: String
)