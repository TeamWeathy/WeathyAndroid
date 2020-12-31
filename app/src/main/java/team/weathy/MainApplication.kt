package team.weathy

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import team.weathy.util.FlipperUtil
import team.weathy.util.PixelRatio
import team.weathy.util.location.LocationUtil


class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initSingletons()
        configureFlipper()
    }

    private fun initSingletons() {
        pixelRatio = PixelRatio(this)
        locationUtil = LocationUtil(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(locationUtil)
    }

    private fun configureFlipper() {
        FlipperUtil.init(this)
    }

    companion object {
        lateinit var pixelRatio: PixelRatio
        lateinit var locationUtil: LocationUtil
    }
}