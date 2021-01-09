package team.weathy.api.mock

import team.weathy.model.entity.Climate
import team.weathy.model.entity.DailyWeather
import team.weathy.model.entity.Date
import team.weathy.model.entity.HourlyWeather
import team.weathy.model.entity.Region
import team.weathy.model.entity.Temperature
import team.weathy.model.entity.User

object MockGenerator {
    fun user() = User(1, "mock user")
    fun region() = Region(1, "region")
    fun climate() = Climate(1, "description")
    fun date() = Date(1, 1, "월요일")
    fun temperature() = Temperature(20, -20)
    fun dailyWeather() = DailyWeather(region(), date(), temperature())
    fun hourlyWeather() = HourlyWeather("time", 17, climate(), 5)
}