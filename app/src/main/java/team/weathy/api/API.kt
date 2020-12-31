package team.weathy.api

import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.weathy.BuildConfig

object API {
    val flipperNetworkPlugin = NetworkFlipperPlugin()

    private val gson = GsonBuilder().create()
    private lateinit var okHttpClient: OkHttpClient

    init {
        configureOkHttpClient()
    }

    private fun configureOkHttpClient() {
        val builder = OkHttpClient.Builder().addInterceptor {
            val headerAddedRequest = it.request().newBuilder().addHeader("Authorization", "JWT 123" /*TODO*/).build()

            it.proceed(headerAddedRequest)
        }
        okHttpClient = if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(FlipperOkhttpInterceptor(flipperNetworkPlugin))
        } else {
            builder
        }.build()
    }

    private val apiRetrofit = Retrofit.Builder().baseUrl("https://api-dev.iammathking.com")
        .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build()

    val sample = apiRetrofit.create(SampleAPI::class.java)
}