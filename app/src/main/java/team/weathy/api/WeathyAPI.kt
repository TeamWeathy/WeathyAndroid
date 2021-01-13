package team.weathy.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import team.weathy.model.entity.Weathy
import team.weathy.util.DateString

data class WeathyRes(
    val weathy: Weathy?, val message: String
)

data class CreateWeathyReq(
    val userId: Int,
    val dateStr: DateString,
    val code: Long,
    val clothes: List<Int>,
    val stampId: Int,
    val feedback: String,
)

data class EditWeathyReq(
    val code: Long,
    val clothes: List<Int>,
    val stampId: Int,
    val feedback: String,
)

interface WeathyAPI {
    @GET("users/$USER_ID_PATH_SEGMENT/weathy/recommend")
    suspend fun fetchRecommendedWeathy(
        @Query("code") code: Long, @Query("date") date: DateString
    ): Response<WeathyRes?>

    @GET("weathy")
    suspend fun fetchWeathyWithDate(
        @Query("date") date: DateString
    ): WeathyRes

    @POST("weathy")
    suspend fun createWeathy(@Body req: CreateWeathyReq): MessageRes

    @PUT("weathy/{weathyId}")
    suspend fun editWeathy(@Path("weathyId") weathyId: Int, @Body req: EditWeathyReq): MessageRes

    @DELETE("weathy/{weathyId}")
    suspend fun deleteWeathy(
        @Path("weathyId") weathyId: Int
    ): MessageRes
}