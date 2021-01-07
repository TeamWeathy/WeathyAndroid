package team.weathy.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import team.weathy.model.entity.DailyWeather
import team.weathy.model.entity.HourlyWeather
import team.weathy.util.DateString


data class WeathyRes(
    val weathy: Weathy?, val message: String
) {
    data class Weathy(
        val dailyWeather: DailyWeather,
        val hourlyWeather: HourlyWeather,
        val closet: Closet,
        val stampId: Int,
        val feedback: String
    ) {

        data class Closet(
            val top: Clothes, val bottom: Clothes, val outer: Clothes, val etc: Clothes
        ) {

            data class Clothes(
                val categoryId: Int, val clothes: List<Cloth>
            ) {
                data class Cloth(
                    val id: Int, val name: String
                )
            }
        }
    }
}

data class CreateWeathyReq(
    val userId: Int,
    val dateStr: DateString,
    val code: Int,
    val clothes: List<Int>,
    val stampId: Int,
    val feedback: String,
)

data class EditWeathyReq(
    val code: Int,
    val clothes: List<Int>,
    val stampId: Int,
    val feedback: String,
)

interface WeathyAPI {
    @GET("users/$USER_ID_PATH_SEGMENT/weathy/recommend")
    fun fetchRecommendedWeathy(
        @Query("code") code: Int, @Query("date") date: DateString
    ): WeathyRes

    @GET("weathy/{weathyId}")
    fun fetchWeathyWithId(
        @Path("weathyId") weathyId: Int
    ): WeathyRes

    @POST("weathy")
    fun createWeathy(@Body req: CreateWeathyReq): MessageRes

    @PUT("weathy/{weathyId}")
    fun editWeathy(@Path("weathyId") weathyId: Int, @Body req: EditWeathyReq): MessageRes

    @DELETE("weathy/{weathyId}")
    fun deleteWeathy(
        @Path("weathyId") weathyId: Int
    ): MessageRes
}