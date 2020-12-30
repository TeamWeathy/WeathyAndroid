package team.weathy

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import team.weathy.util.PixelRatio
import team.weathy.util.location.LocationUtil

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initSingletons()
    }

    private fun initSingletons() {
        pixelRatio = PixelRatio(this)
        locationUtil = LocationUtil(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(locationUtil)
    }

    companion object {
        lateinit var pixelRatio: PixelRatio
        lateinit var locationUtil: LocationUtil
    }
}