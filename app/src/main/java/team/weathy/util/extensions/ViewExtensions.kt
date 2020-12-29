package team.weathy.util.extensions

import android.os.Build.VERSION
import android.view.View

val View.isShadowColorAvailable: Boolean
    get() = VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P

fun View.setShadowColorIfAvailable(color: Int) {
    if (isShadowColorAvailable) {
        outlineSpotShadowColor = color
        outlineAmbientShadowColor = color
    }
}