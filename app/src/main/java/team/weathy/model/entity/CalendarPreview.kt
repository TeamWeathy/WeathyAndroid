package team.weathy.model.entity

data class CalendarPreview(
    val id: Int, val stampId: WeatherStamp, val climateIconId : Weather, val temperature: Temperature,
)