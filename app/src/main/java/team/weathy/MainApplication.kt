package team.weathy

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
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
        SoLoader.init(this, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(NetworkFlipperPlugin())
            client.start()
        }
    }

    companion object {
        lateinit var pixelRatio: PixelRatio
        lateinit var locationUtil: LocationUtil
    }
}