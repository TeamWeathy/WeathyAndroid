package team.weathy

import android.app.Application
import team.weathy.util.PixelRatio

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