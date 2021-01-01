package team.weathy.util

import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

object AnimUtil {
    fun runSpringAnimation(startValue: Float, finalValue: Float, multiplier: Float, callback: (value: Float) -> Unit) {
        SpringAnimation(FloatValueHolder()).apply {
            setStartValue(startValue * multiplier)
            spring = SpringForce(finalValue * multiplier).apply {
                stiffness = SpringForce.STIFFNESS_LOW
            }
            addUpdateListener { _, value, _ ->
                callback(value / multiplier)
            }
        }.start()
    }
}