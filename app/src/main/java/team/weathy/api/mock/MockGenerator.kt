package team.weathy.api.mock

import com.thedeanda.lorem.LoremIpsum
import team.weathy.api.FetchCalendarPreviewRes
import team.weathy.model.entity.CalendarPreview
import team.weathy.model.entity.Climate
import team.weathy.model.entity.DailyWeather
import team.weathy.model.entity.Date
import team.weathy.model.entity.HourlyWeather
import team.weathy.model.entity.Region
import team.weathy.model.entity.Temperature
import team.weathy.model.entity.User
import team.weathy.model.entity.WeatherStamp
import team.weathy.model.entity.Weathy
import team.weathy.model.entity.WeathyCloset
import team.weathy.model.entity.WeathyClothes
import team.weathy.model.entity.WeathyClothes.Cloth
import kotlin.random.Random

object MockGenerator {
    fun user(id: Int = 1, nickname: String = "유저") = User(id, nickname)
    fun region(code: Int = Random.nextInt(), name: String = LoremIpsum.getInstance().city) = Region(code, name)
    fun climate(iconId: Int = 1, description: String = LoremIpsum.getInstance().name) = Climate(iconId, description)
    fun date(month: Int = 1, day: Int = 1, dayOfWeek: String = "월요일") = Date(month, day, dayOfWeek)
    fun temperature() = Temperature(Random.nextInt(0, 20), Random.nextInt(-20, 0))
    fun dailyWeather(code: Int = Random.nextInt(), regionName: String = LoremIpsum.getInstance().city) =
        DailyWeather(date(), temperature())

    fun hourlyWeather() = HourlyWeather("time", 17, climate(), 5)

    fun weahtyCloset() = WeathyCloset(
        WeathyClothes(1, (0 until Random.nextInt(2, 7)).map {
            Cloth(it, LoremIpsum.getInstance().firstName)
        }),
        WeathyClothes(1, (0 until Random.nextInt(2, 6)).map {
            Cloth(it, LoremIpsum.getInstance().firstName)
        }),
        WeathyClothes(1, (0 until Random.nextInt(2, 7)).map {
            Cloth(it, LoremIpsum.getInstance().firstName)
        }),
        WeathyClothes(1, (0 until Random.nextInt(2, 6)).map {
            Cloth(it, LoremIpsum.getInstance().firstName)
        }),
    )

    fun weathy() = Weathy(
        region(),
        dailyWeather(),
        hourlyWeather(),
        weahtyCloset(),
        WeatherStamp.values().random(),
        LoremIpsum.getInstance().getParagraphs(2, 4)
    )

    fun calendarPreview() =
        CalendarPreview(1, WeatherStamp.values().random(), Temperature(Random.nextInt(21), -Random.nextInt(21)))

    fun calendarPreviews(size: Int): FetchCalendarPreviewRes {
        val list = mutableListOf<CalendarPreview?>()
        repeat(size) {
            list.add(if (Random.nextInt(0, 2) == 0) null else calendarPreview())
        }
        return FetchCalendarPreviewRes(list, "message")
    }
}