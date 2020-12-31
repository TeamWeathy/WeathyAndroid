package team.weathy.api

import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object API {
    private val gson = GsonBuilder().create()
    private val okHttpClient = OkHttpClient.Builder().addNetworkInterceptor(HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }).addNetworkInterceptor(FlipperOkhttpInterceptor(NetworkFlipperPlugin())).addInterceptor {
        val headerAddedRequest = it.request().newBuilder().addHeader("token", "/*Header*/" /*TODO*/).build()

        it.proceed(headerAddedRequest)
    }.build()

    private val apiRetrofit =
        Retrofit.Builder().baseUrl("/*BaseURL*/" /*TODO*/).addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient).build()

    val sample = apiRetrofit.create(SampleAPI::class.java)
}