package team.weathy.model.entity

import androidx.annotation.DrawableRes
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.annotations.JsonAdapter
import team.weathy.R
import team.weathy.api.ApiSerializer
import java.lang.reflect.Type

@JsonAdapter(WeatherSerizlier::class)
enum class Weather(val id: Int, @DrawableRes val iconId: Int) {
    CLEAR_SKY(
        1, R.drawable.ic_clearsky_day
    ),
    FEW_CLOUDS(
        2, R.drawable.ic_fewclouds_day
    ),
    SCATTERED_CLOUDS(
        3, R.drawable.ic_scatteredclouds
    ),
    BROKEN_CLOUDS(4, R.drawable.ic_brokenclouds), SHOWER_RAIN(
        9, R.drawable.ic_showerrain
    ),
    THUNDERSTOME(11, R.drawable.ic_thunderstorm), RAIN(10, R.drawable.ic_rain_day), SNOW(
        13, R.drawable.ic_snow
    ),
    MIST(
        50, R.drawable.ic_mist
    ),
    CLEAR_SKY_NIGHT(
        101, R.drawable.ic_clearsky_night
    ),
    FEW_CLOUDS_NIGHT(
        102, R.drawable.ic_fewclouds_night
    ),
    SCATTERED_CLOUDS_NIGHT(103, R.drawable.ic_scatteredclouds), BROKEN_CLOUDS_NIGHT(
        104, R.drawable.ic_brokenclouds
    ),
    SHOWER_RAIN_NIGHT(
        109, R.drawable.ic_showerrain
    ),
    THUNDERSTOME_NIGHT(111, R.drawable.ic_thunderstorm), RAIN_NIGHT(
        110, R.drawable.ic_rain_night
    ),
    SNOW_NIGHT(113, R.drawable.ic_snow), MIST_NIGHT(150, R.drawable.ic_mist), ;

    companion object {
        fun withId(id: Int?): Weather {
            return values().find { it.id == id } ?: CLEAR_SKY
        }
    }
}

class WeatherSerizlier : ApiSerializer<Weather> {
    override fun serialize(src: Weather, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Weather {
        return Weather.withId(json?.asInt)
    }
}