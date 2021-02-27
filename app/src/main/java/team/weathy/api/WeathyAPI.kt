package team.weathy.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import team.weathy.model.entity.Weathy
import team.weathy.util.DateString

data class WeathyRes(
    val weathy: Weathy?, val message: String
)

data class CreateWeathyReq(
    val userId: Int,
    @SerializedName("date") val dateStr: DateString,
    val code: Long,
    val clothes: List<Int>,
    val stampId: Int,
    val feedback: String?,
)

data class EditWeathyReq(
    val code: Long,
    val clothes: List<Int>,
    val stampId: Int,
    val feedback: String?,
    val isDelete: Boolean
)

interface WeathyAPI {
    @GET("users/$USER_ID_PATH_SEGMENT/weathy/rec    ommend")
    suspend fun fetchRecommendedWeathy(
        @Query("code") code: Long, @Query("date") date: DateString
    ): Response<WeathyRes?>

    @GET("weathy")
    suspend fun fetchWeathyWithDate(
        @Query("date") date: DateString
    ): WeathyRes

    @Multipart
    @POST("weathy")
    suspend fun createWeathy(
        @Part("weathy") weathy: RequestBody,
        @Part img: MultipartBody.Part?
    ): MessageIdRes

    @Multipart
    @PUT("weathy/{weathyId}")
    suspend fun editWeathy(
        @Path("weathyId") weathyId: Int,
        @Part("weathy") weathy: RequestBody,
        @Part img: MultipartBody.Part?
    ): MessageRes

    @DELETE("weathy/{weathyId}")
    suspend fun deleteWeathy(
        @Path("weathyId") weathyId: Int
    ): MessageRes
}