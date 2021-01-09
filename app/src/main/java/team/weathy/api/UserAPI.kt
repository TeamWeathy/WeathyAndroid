package team.weathy.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import team.weathy.model.entity.User

data class CreateUserReq(
    val uuid: String, val nickname: String,
)

data class EditUserReq(
    val nickname: String
)

data class UserRes(
    @SerializedName("user") val user: User,
    @SerializedName("token") val token: String,
    @SerializedName("message") val message: String
)

interface UserAPI {
    @POST("users")
    suspend fun createUser(@Body req: CreateUserReq): UserRes

    @PUT("users/$USER_ID_PATH_SEGMENT")
    suspend fun editUser(@Body req: EditUserReq): UserRes
}