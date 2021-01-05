package team.weathy

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import team.weathy.util.FlipperUtil
import team.weathy.util.PixelRatio
import team.weathy.util.SPUtil
import team.weathy.util.UniqueIdentifier
import team.weathy.util.location.LocationUtil


class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeSingletons()
        configureFlipper()
    }

    /**
     * Simple and easy dependency injection :)
     */
    private fun initializeSingletons() {
        pixelRatio = PixelRatio(this)
        locationUtil = LocationUtil(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(locationUtil)
        spUtil = SPUtil(this)
        uniqueId = UniqueIdentifier(spUtil)
    }

    private fun configureFlipper() {
        FlipperUtil.init(this)
    }

    companion object {
        lateinit var pixelRatio: PixelRatio
        lateinit var locationUtil: LocationUtil
        lateinit var spUtil: SPUtil
        lateinit var uniqueId: UniqueIdentifier
    }
}