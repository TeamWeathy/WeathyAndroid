package team.weathy.util.extensions

import android.os.Build.VERSION
import android.view.View
import androidx.annotation.Px
import kotlin.math.roundToInt

val View.isShadowColorAvailable: Boolean
    get() = VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P

fun View.setShadowColorIfAvailable(color: Int) {
    if (isShadowColorAvailable) {
        outlineSpotShadowColor = color
        outlineAmbientShadowColor = color
    }
}

@Px
fun View.px(dp: Int) = (dp * resources.displayMetrics.density).roundToInt()
fun View.pxFloat(dp: Int) = (dp * resources.displayMetrics.density)
fun View.spPx(dp: Int) = (dp * resources.displayMetrics.scaledDensity)
val View.screenHeight: Int
    get() = resources.displayMetrics.heightPixels