package team.weathy.model.entity

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.annotations.JsonAdapter
import team.weathy.api.ApiSerializer
import java.lang.reflect.Type

@JsonAdapter(WeatherHumiditySerializer::class)
enum class WeatherHumidity(val id: Int, val representation: String) {
    LEVEL1(1, "안습함"), LEVEL2(2, "조금습함"), LEVEL3(3, "보통"), LEVEL4(4, "조금많이습합"), LEVEL5(5, "많이습함"),

    ;

    companion object {
        fun fromId(id: Int?): WeatherHumidity? {
            return values().find { it.id == id }
        }
    }
}

class WeatherHumiditySerializer : ApiSerializer<WeatherHumidity> {
    override fun serialize(src: WeatherHumidity, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): WeatherHumidity {
        return WeatherHumidity.fromId(json?.asInt) ?: WeatherHumidity.values().first()
    }
}