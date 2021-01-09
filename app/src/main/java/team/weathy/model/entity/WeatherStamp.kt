package team.weathy.model.entity

import androidx.annotation.ColorRes
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.annotations.JsonAdapter
import team.weathy.R
import team.weathy.api.ApiSerializer
import java.lang.reflect.Type

@JsonAdapter(WeatherStampSerializer::class)
enum class WeatherStamp(val id: Int, @ColorRes val colorRes: Int) {
    VERY_HOT(1, R.color.imoji_veryhot), HOT(2, R.color.imoji_hot), GOOD(3, R.color.imoji_good), COLD(
        4, R.color.imoji_cold
    ),
    VERY_COLD(5, R.color.imoji_verycold),

    ;

    companion object {
        fun fromId(id: Int): WeatherStamp? {
            return values().find { it.id == id }
        }
    }
}

class WeatherStampSerializer : ApiSerializer<WeatherStamp> {
    override fun serialize(src: WeatherStamp?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src?.id?.let { JsonPrimitive(it) }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): WeatherStamp? {
        return WeatherStamp.fromId(json.asInt)
    }
}