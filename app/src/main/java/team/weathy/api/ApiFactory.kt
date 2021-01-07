package team.weathy.api

import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.weathy.MainApplication.Companion.uniqueId
import team.weathy.util.FlipperUtil
import javax.inject.Inject
import kotlin.reflect.KClass

const val USER_ID_PATH_SEGMENT = "__USER_ID_PATH_SEGMENT__"

class ApiFactory @Inject constructor() {
    private val gson = GsonBuilder().create()
    private lateinit var okHttpClient: OkHttpClient

    init {
        configureOkHttpClient()
    }

    private fun configureOkHttpClient() {
        val builder = OkHttpClient.Builder().addInterceptor {
            val baseRequest = it.request()
            val builder = baseRequest.newBuilder()

            it.proceed(builder.url(baseRequest.url().fillUserId()).addHeaders().build())
        }
        okHttpClient = FlipperUtil.addFlipperNetworkPlguin(builder).build()
    }

    private fun HttpUrl.fillUserId() = if (toString().contains(USER_ID_PATH_SEGMENT)) {
        val idx = pathSegments().indexOf(USER_ID_PATH_SEGMENT)
        newBuilder().setPathSegment(idx, uniqueId.id).build()
    } else {
        this
    }

    private fun Request.Builder.addHeaders() =
        addHeader("Content-Type", "application/json").addHeader("Accept", "application/json")
            .addHeader("x-access-token", "JWT 123" /*TODO*/)

    private val apiRetrofit =
        Retrofit.Builder().baseUrl("https://www.google.com" /*TODO*/).addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient).build()

    fun <T : Any> createApi(clazz: KClass<T>): T = apiRetrofit.create(clazz.java)
}