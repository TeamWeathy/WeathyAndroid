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
    LEVEL1(1, "매우 건조"), LEVEL2(2, "건조"), LEVEL3(3, "적정"), LEVEL4(4, "습함"), LEVEL5(5, "매우 습함"),

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