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

    @Inject
    lateinit var pixelRatio: PixelRatio

    override fun onCreate() {
        super.onCreate()
        initializeSingletons()
        configureFlipper()
    }

    private fun initializeSingletons() {
        MainApplication.pixelRatio = this.pixelRatio
        ProcessLifecycleOwner.get().lifecycle.addObserver(locationUtil)
    }

    private fun configureFlipper() {
        FlipperUtil.init(this)
    }

    companion object {
        lateinit var pixelRatio: PixelRatio
    }
}