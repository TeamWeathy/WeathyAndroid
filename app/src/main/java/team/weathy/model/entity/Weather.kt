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
    @DrawableRes val HomeBackgroundId: Int,
    @DrawableRes val topBlurId: Int,
    @DrawableRes val SearchBackgroundId: Int,
    val backgroundAnimation: BackgroundAnimation? = null,
) {
    CLEAR_SKY(
        1,
        R.drawable.ic_clearsky_day,
        R.drawable.main_img_clearsky_day,
        R.drawable.record_img_clearsky_day,
        R.drawable.main_bg_morning_sun,
        R.drawable.mainscroll_box_topblur_morning,
        R.drawable.search_bg_morning,
    ),
    FEW_CLOUDS(
        2,
        R.drawable.ic_fewclouds_day,
        R.drawable.main_img_fewclouds_day,
        R.drawable.record_img_fewclouds_day,
        R.drawable.main_bg_morning_sun,
        R.drawable.mainscroll_box_topblur_morning,
        R.drawable.search_bg_morning,
    ),
    SCATTERED_CLOUDS(
        3,
        R.drawable.ic_scatteredclouds,
        R.drawable.main_img_scatteredclouds_day,
        R.drawable.record_img_scatteredclouds,
        R.drawable.main_bg_morning,
        R.drawable.mainscroll_box_topblur_morning,
        R.drawable.search_bg_morning,
    ),
    BROKEN_CLOUDS(
        4,
        R.drawable.ic_brokenclouds,
        R.drawable.main_img_brokenclouds_day,
        R.drawable.record_img_brokenclouds,
        R.drawable.main_bg_cloudy,
        R.drawable.mainscroll_box_topblur_snowrain,
        R.drawable.search_bg_snowrain,
    ),
    SHOWER_RAIN(
        9,
        R.drawable.ic_showerrain,
        R.drawable.main_img_showerrain_day,
        R.drawable.record_img_showerrain_day,
        R.drawable.main_bg_snowrain_day,
        R.drawable.mainscroll_box_topblur_snowrain,
        R.drawable.search_bg_snowrain,
        BackgroundAnimation.RAIN,
    ),
    RAIN(
        10,
        R.drawable.ic_rain_day,
        R.drawable.main_img_rain_day,
        R.drawable.record_img_rain,
        R.drawable.main_bg_snowrain_day,
        R.drawable.mainscroll_box_topblur_snowrain,
        R.drawable.search_bg_snowrain,
        BackgroundAnimation.RAIN,
    ),
    THUNDERSTOME(
        11,
        R.drawable.ic_thunderstorm,
        R.drawable.main_img_thunderstorm_day,
        R.drawable.record_img_thunderstorm,
        R.drawable.main_bg_cloudy,
        R.drawable.mainscroll_box_topblur_snowrain,
        R.drawable.search_bg_snowrain,
    ),

    SNOW(
        13,
        R.drawable.ic_snow,
        R.drawable.main_img_snow_day,
        R.drawable.record_img_snow,
        R.drawable.main_bg_snowrain_day,
        R.drawable.mainscroll_box_topblur_snowrain,
        R.drawable.search_bg_snowrain,
        BackgroundAnimation.SNOW,
    ),
    MIST(
        50,
        R.drawable.ic_mist,
        R.drawable.main_img_mist_day,
        R.drawable.record_img_mist,
        R.drawable.main_bg_cloudy,
        R.drawable.mainscroll_box_topblur_snowrain,
        R.drawable.search_bg_snowrain,
    ),
    CLEAR_SKY_NIGHT(
        101,
        R.drawable.ic_clearsky_night,
        R.drawable.main_img_clearsky_night,
        R.drawable.record_img_clearsky_night,
        R.drawable.main_bg_night_star,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
    ),
    FEW_CLOUDS_NIGHT(
        102,
        R.drawable.ic_fewclouds_night,
        R.drawable.main_img_fewclouds_night,
        R.drawable.record_img_fewclouds_night,
        R.drawable.main_bg_night_star,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
    ),
    SCATTERED_CLOUDS_NIGHT(
        103,
        R.drawable.ic_scatteredclouds,
        R.drawable.main_img_scatteredclouds_night,
        R.drawable.record_img_scatteredclouds,
        R.drawable.main_bg_night,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
    ),
    BROKEN_CLOUDS_NIGHT(
        104,
        R.drawable.ic_brokenclouds,
        R.drawable.main_img_brokenclouds_night,
        R.drawable.record_img_brokenclouds,
        R.drawable.main_bg_night,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
    ),
    SHOWER_RAIN_NIGHT(
        109,
        R.drawable.ic_showerrain,
        R.drawable.main_img_showerrain_night,
        R.drawable.record_img_showerrain_night,
        R.drawable.main_bg_snowrain_night,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
        BackgroundAnimation.RAIN,
    ),
    THUNDERSTOME_NIGHT(
        111,
        R.drawable.ic_thunderstorm,
        R.drawable.main_img_thunderstorm_night,
        R.drawable.record_img_thunderstorm,
        R.drawable.main_bg_night,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
    ),
    RAIN_NIGHT(
        110,
        R.drawable.ic_rain_night,
        R.drawable.main_img_rain_night,
        R.drawable.record_img_rain,
        R.drawable.main_bg_snowrain_night,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
        BackgroundAnimation.RAIN,
    ),
    SNOW_NIGHT(
        113,
        R.drawable.ic_snow,
        R.drawable.main_img_snow_night,
        R.drawable.record_img_snow,
        R.drawable.main_bg_snowrain_night,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
        BackgroundAnimation.SNOW,
    ),
    MIST_NIGHT(
        150,
        R.drawable.ic_mist,
        R.drawable.main_img_mist_night,
        R.drawable.record_img_mist,
        R.drawable.main_bg_night,
        R.drawable.mainscroll_box_topblur_night,
        R.drawable.search_bg_night,
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