package team.weathy.model.entity

data class CalendarPreview(
    val id: Int, val stampId: Int, val temperature: Temperature,
) {
    data class Temperature(
        val maxTemp: Int,
        val minTemp: Int,
    )
}