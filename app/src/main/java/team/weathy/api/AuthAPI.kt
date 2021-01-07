package team.weathy.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import team.weathy.model.entity.User


data class LoginReq(
    val uuid: String
)

data class LoginRes(
    @SerializedName("user") val user: User,
    @SerializedName("token") val token: String,
    @SerializedName("message") val message: String
)

interface AuthAPI {
    @POST("auth/login")
    suspend fun login(@Body body: LoginReq): LoginRes
}