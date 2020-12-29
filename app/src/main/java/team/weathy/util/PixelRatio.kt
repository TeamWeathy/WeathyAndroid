package team.weathy.util

import android.app.Application
import androidx.annotation.Px
import team.weathy.MainApplication
import kotlin.math.roundToInt

class PixelRatio(private val app: Application) {
    private val displayMetrics
        get() = app.resources.displayMetrics

    val screenWidth
        get() = displayMetrics.widthPixels

    val screenHeight
        get() = displayMetrics.heightPixels

    @Px
    fun toPixel(dp: Int) = (dp * displayMetrics.density).roundToInt()

    fun toDP(@Px pixel: Int) = (pixel / displayMetrics.density).roundToInt()
}

val Number.pixel: Int
    @Px get() = MainApplication.pixelRatio.toDP(this.toInt())

val Number.dp: Int
    get() = MainApplication.pixelRatio.toPixel(this.toInt())

val Number.pixelFloat: Float
    @Px get() = MainApplication.pixelRatio.toDP(this.toInt()).toFloat()

val Number.dpFloat: Float
    get() = MainApplication.pixelRatio.toPixel(this.toInt()).toFloat()