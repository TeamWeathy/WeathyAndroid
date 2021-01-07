package team.weathy

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import team.weathy.util.FlipperUtil
import team.weathy.util.PixelRatio
import team.weathy.util.location.LocationUtil
import javax.inject.Inject


@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var locationUtil: LocationUtil

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
        ProcessLifecycleOwner.get().lifecycle.addObserver(locationUtil)
    }

    private fun configureFlipper() {
        FlipperUtil.init(this)
    }

    companion object {
        lateinit var pixelRatio: PixelRatio
    }
}