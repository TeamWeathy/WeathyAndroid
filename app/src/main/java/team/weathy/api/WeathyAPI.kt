package team.weathy.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*
import team.weathy.model.entity.Weathy
import team.weathy.util.DateString
import java.io.File

data class WeathyRes(
    val weathy: Weathy?, val message: String
)

data class CreateWeathyReq(
    val weathy: RecordWeathy, val img: File
) {
    data class RecordWeathy(
        val userId: Int,
        @SerializedName("date") val dateStr: DateString,
        val code: Long,
        val clothes: List<Int>,
        val stampId: Int,
        val feedback: String?
    )
}

data class EditWeathyReq(
    val weathy: EditWeathy, val img: File
) {
    data class EditWeathy(
        val code: Long,
        val clothes: List<Int>,
        val stampId: Int,
        val feedback: String?,
        val isDelete: Boolean
    )
}

interface WeathyAPI {
    @GET("users/$USER_ID_PATH_SEGMENT/weathy/recommend")
    suspend fun fetchRecommendedWeathy(
        @Query("code") code: Long, @Query("date") date: DateString
    ): Response<WeathyRes?>

    @GET("weathy")
    suspend fun fetchWeathyWithDate(
        @Query("date") date: DateString
    ): WeathyRes

    @Multipart
    @POST("weathy")
    suspend fun createWeathy(@Body req: CreateWeathyReq): MessageRes

    @Multipart
    @PUT("weathy/{weathyId}")
    suspend fun editWeathy(@Path("weathyId") weathyId: Int, @Body req: EditWeathyReq): MessageRes

    @DELETE("weathy/{weathyId}")
    suspend fun deleteWeathy(
        @Path("weathyId") weathyId: Int
    ): MessageRes
}