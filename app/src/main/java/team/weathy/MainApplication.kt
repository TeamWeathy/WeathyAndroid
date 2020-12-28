package team.weathy

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initSingletons()
    }

    private fun initSingletons() {
        pixelRatio = PixelRatio(this)
    }

    companion object {
        lateinit var pixelRatio: PixelRatio
    }
}