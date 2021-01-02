package team.weathy.util

import android.app.Application
import okhttp3.OkHttpClient

object FlipperUtil {
    fun init(app: Application) {}

    fun addFlipperNetworkPlguin(builder: OkHttpClient.Builder) = builder
}