package team.weathy.model.entity

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.annotations.JsonAdapter
import team.weathy.api.ApiSerializer
import java.lang.reflect.Type

@JsonAdapter(WeatherRainSerializer::class)
enum class WeatherRain(val id: Int, val representation: String) {
    LEVEL1(1, "매우 조금"), LEVEL2(2, "조금"), LEVEL3(3, "보통"), LEVEL4(4, "다소 많음"), LEVEL5(5, "많음"), LEVEL6(6, "매우 많음")


    ;

    companion object {
        fun fromId(id: Int?): WeatherRain? {
            return values().find { it.id == id }
        }
    }
}

class WeatherRainSerializer : ApiSerializer<WeatherRain> {
    override fun serialize(src: WeatherRain, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): WeatherRain {
        return WeatherRain.fromId(json?.asInt) ?: WeatherRain.values().first()
    }
}