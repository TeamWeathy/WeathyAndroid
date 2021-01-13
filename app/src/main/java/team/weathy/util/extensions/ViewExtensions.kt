package team.weathy.util.extensions

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Build.VERSION
import android.view.View
import android.widget.Button
import androidx.annotation.Px
import androidx.core.animation.doOnEnd
import team.weathy.R
import team.weathy.util.debugE
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

fun Button.enableWithAnim(enable: Boolean) {
    if (isEnabled == enable) return

    isEnabled = enable
    val colorChangeActive = AnimatorInflater.loadAnimator(
        context, if (enable) R.animator.color_change_active_anim else R.animator.color_change_anim
    ) as AnimatorSet
    colorChangeActive.setTarget(this)
    colorChangeActive.start()
}
