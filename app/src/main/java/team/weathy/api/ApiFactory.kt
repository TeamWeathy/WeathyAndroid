package team.weathy.api

import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.weathy.util.FlipperUtil
import team.weathy.util.UniqueIdentifier
import javax.inject.Inject
import kotlin.reflect.KClass


const val USER_ID_PATH_SEGMENT = "__USER_ID_PATH_SEGMENT__"

class ApiFactory @Inject constructor(private val uniqueId: UniqueIdentifier) {
    private val gson = GsonBuilder().create()
    private lateinit var okHttpClient: OkHttpClient

    init {
        configureOkHttpClient()
    }

    private fun configureOkHttpClient() {
        val builder = OkHttpClient.Builder().addInterceptor {
            val baseRequest = it.request()
            val builder = baseRequest.newBuilder()

            it.proceed(builder.url(baseRequest.url().fillUserId()).addHeaders(uniqueId.loadToken()).build())
        }
        okHttpClient = FlipperUtil.addFlipperNetworkPlguin(builder).build()
    }

    private fun HttpUrl.fillUserId() = if (toString().contains(USER_ID_PATH_SEGMENT)) {
        val idx = pathSegments().indexOf(USER_ID_PATH_SEGMENT)
        newBuilder().setPathSegment(idx, uniqueId.userId.toString()).build()
    } else {
        this
    }

    private fun Request.Builder.addHeaders(token: String?): Request.Builder {
        addHeader("Content-Type", "application/json").addHeader("Accept", "application/json")
        token?.let {
            addHeader("x-access-token", "${uniqueId.loadToken()}")
        }
        return this
    }

    private val apiRetrofit =
        Retrofit.Builder().baseUrl("http://15.164.146.132:3000").addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient).build()

    fun <T : Any> createApi(clazz: KClass<T>): T = apiRetrofit.create(clazz.java)
}