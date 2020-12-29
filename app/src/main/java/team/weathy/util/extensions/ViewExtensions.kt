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
fun View.toPixel(dp: Int) = (dp * resources.displayMetrics.density).roundToInt()

fun View.toDP(@Px pixel: Int) = (pixel / resources.displayMetrics.density).roundToInt()