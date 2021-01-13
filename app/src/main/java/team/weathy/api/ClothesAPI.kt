package team.weathy.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import team.weathy.model.entity.ClothCategory
import team.weathy.model.entity.WeathyCloset


data class ClothesRes(
    val closet: WeathyCloset, val message: String
)

data class CreateClothesReq(
    val category: ClothCategory, val name: String
)

data class CreateClothesRes(
    @SerializedName("clothesList") val closet: WeathyCloset, @SerializedName("message") val message: String
)

data class DeleteClothesReq(
    val clothes: List<Int>
)

interface ClothesAPI {
    @GET("users/$USER_ID_PATH_SEGMENT/clothes")
    suspend fun getClothes(): ClothesRes

    @POST("users/$USER_ID_PATH_SEGMENT/clothes")
    suspend fun createClothes(@Body req: CreateClothesReq): CreateClothesRes

    @HTTP(method = "DELETE", path = "users/$USER_ID_PATH_SEGMENT/clothes", hasBody = true)
    suspend fun deleteClothes(@Body req: DeleteClothesReq): ClothesRes
}