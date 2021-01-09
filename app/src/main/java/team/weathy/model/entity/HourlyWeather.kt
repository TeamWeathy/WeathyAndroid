package team.weathy.model.entity

data class HourlyWeather(
    val time: String?, val temperature: Int?, val climate: Climate, val pop: Int
)