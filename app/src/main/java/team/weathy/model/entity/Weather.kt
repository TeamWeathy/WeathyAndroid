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
enum class Weather(
    val id: Int,
    @DrawableRes val smallIconId: Int,
    @DrawableRes val bigIconId: Int,
    @DrawableRes val mediumIconId: Int,
    @DrawableRes val firstHomeBackgroundId: Int,
    @DrawableRes val secondHomeBackgroundId: Int,
    val backgroundAnimation: BackgroundAnimation? = null,
) {
    CLEAR_SKY(
        1,
        R.drawable.ic_clearsky_day,
        R.drawable.main_img_clearsky_day,
        R.drawable.record_img_clearsky_day,
        R.drawable.mainscroll_bg_morning,
        R.drawable.main_bg_morning_sun,
    ),
    FEW_CLOUDS(
        2,
        R.drawable.ic_fewclouds_day,
        R.drawable.main_img_fewclouds_day,
        R.drawable.record_img_fewclouds_day,
        R.drawable.mainscroll_bg_morning,
        R.drawable.main_bg_morning_sun,
    ),
    SCATTERED_CLOUDS(
        3,
        R.drawable.ic_scatteredclouds,
        R.drawable.main_img_scatteredclouds_day,
        R.drawable.record_img_scatteredclouds,
        R.drawable.mainscroll_bg_morning,
        R.drawable.main_bg_morning,
    ),
    BROKEN_CLOUDS(
        4,
        R.drawable.ic_brokenclouds,
        R.drawable.main_img_brokenclouds_day,
        R.drawable.record_img_brokenclouds,
        R.drawable.mainscroll_bg_snowrain,
        R.drawable.main_bg_cloudy,
    ),
    SHOWER_RAIN(
        9,
        R.drawable.ic_showerrain,
        R.drawable.main_img_showerrain_day,
        R.drawable.record_img_showerrain_day,
        R.drawable.mainscroll_bg_snowrain,
        R.drawable.main_bg_snowrain_day,
        BackgroundAnimation.RAIN,
    ),
    RAIN(
        10,
        R.drawable.ic_rain_day,
        R.drawable.main_img_rain_day,
        R.drawable.record_img_rain,
        R.drawable.mainscroll_bg_snowrain,
        R.drawable.main_bg_snowrain_day,
        BackgroundAnimation.RAIN,
    ),
    THUNDERSTOME(
        11,
        R.drawable.ic_thunderstorm,
        R.drawable.main_img_thunderstorm_day,
        R.drawable.record_img_thunderstorm,
        R.drawable.mainscroll_bg_snowrain,
        R.drawable.main_bg_cloudy,
    ),

    SNOW(
        13,
        R.drawable.ic_snow,
        R.drawable.main_img_snow_day,
        R.drawable.record_img_snow,
        R.drawable.mainscroll_bg_snowrain,
        R.drawable.main_bg_snowrain_day,
        BackgroundAnimation.SNOW,
    ),
    MIST(
        50,
        R.drawable.ic_mist,
        R.drawable.main_img_mist_day,
        R.drawable.record_img_mist,
        R.drawable.mainscroll_bg_snowrain,
        R.drawable.main_bg_cloudy,
    ),
    CLEAR_SKY_NIGHT(
        101,
        R.drawable.ic_clearsky_night,
        R.drawable.main_img_clearsky_night,
        R.drawable.record_img_clearsky_night,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_night_star,
    ),
    FEW_CLOUDS_NIGHT(
        102,
        R.drawable.ic_fewclouds_night,
        R.drawable.main_img_fewclouds_night,
        R.drawable.record_img_fewclouds_night,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_night_star,
    ),
    SCATTERED_CLOUDS_NIGHT(
        103,
        R.drawable.ic_scatteredclouds,
        R.drawable.main_img_scatteredclouds_night,
        R.drawable.record_img_scatteredclouds,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_night,
    ),
    BROKEN_CLOUDS_NIGHT(
        104,
        R.drawable.ic_brokenclouds,
        R.drawable.main_img_brokenclouds_night,
        R.drawable.record_img_brokenclouds,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_night,
    ),
    SHOWER_RAIN_NIGHT(
        109,
        R.drawable.ic_showerrain,
        R.drawable.main_img_showerrain_night,
        R.drawable.record_img_showerrain_night,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_snowrain_night,
        BackgroundAnimation.RAIN,
    ),
    THUNDERSTOME_NIGHT(
        111,
        R.drawable.ic_thunderstorm,
        R.drawable.main_img_thunderstorm_night,
        R.drawable.record_img_thunderstorm,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_night,
    ),
    RAIN_NIGHT(
        110,
        R.drawable.ic_rain_night,
        R.drawable.main_img_rain_night,
        R.drawable.record_img_rain,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_snowrain_night,
        BackgroundAnimation.RAIN,
    ),
    SNOW_NIGHT(
        113,
        R.drawable.ic_snow,
        R.drawable.main_img_snow_night,
        R.drawable.record_img_snow,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_snowrain_night,
        BackgroundAnimation.SNOW,
    ),
    MIST_NIGHT(
        150,
        R.drawable.ic_mist,
        R.drawable.main_img_mist_night,
        R.drawable.record_img_mist,
        R.drawable.mainscroll_bg_night,
        R.drawable.main_bg_night,
    ), ;


    enum class BackgroundAnimation {
        RAIN, SNOW
    }

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