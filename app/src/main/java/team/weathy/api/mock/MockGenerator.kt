package team.weathy.api.mock

import team.weathy.model.entity.Climate
import team.weathy.model.entity.DailyWeather
import team.weathy.model.entity.Date
import team.weathy.model.entity.HourlyWeather
import team.weathy.model.entity.Region
import team.weathy.model.entity.Temperature
import team.weathy.model.entity.User

object MockGenerator {
    fun user(id: Int = 1, nickname: String = "유저") = User(id, nickname)
    fun region(code: Int = 1, name: String = "이름") = Region(code, name)
    fun climate(iconId: Int = 1, description: String = "설명") = Climate(iconId, description)
    fun date(month: Int = 1, day: Int = 1, dayOfWeek: String = "월요일") = Date(month, day, dayOfWeek)
    fun temperature() = Temperature(20, -20)
    fun dailyWeather(regionName: String = "region") = DailyWeather(region(name = regionName), date(), temperature())
    fun hourlyWeather() = HourlyWeather("time", 17, climate(), 5)
}