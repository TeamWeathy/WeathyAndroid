package team.weathy.model.entity

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.annotations.JsonAdapter
import team.weathy.api.ApiSerializer
import java.lang.reflect.Type

@JsonAdapter(WeatherWindSerializer::class)
enum class WeatherWind(val id: Int, val representation: String) {
    LEVEL1(1, "매우 약함"), LEVEL2(2, "약함"), LEVEL3(3, "보통"), LEVEL4(4, "다소 강함"), LEVEL5(5, "강함"), LEVEL6(6, "매우 강함")

    ;

    companion object {
        fun fromId(id: Int?): WeatherWind? {
            return values().find { it.id == id }
        }
    }
}

class WeatherWindSerializer : ApiSerializer<WeatherWind> {
    override fun serialize(src: WeatherWind, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): WeatherWind {
        return WeatherWind.fromId(json?.asInt) ?: WeatherWind.values().first()
    }
}