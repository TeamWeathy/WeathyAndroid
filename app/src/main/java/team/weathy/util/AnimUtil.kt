package team.weathy.util

import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

object AnimUtil {
    fun runSpringAnimation(
        startValue: Float,
        finalValue: Float,
        stiffness: Float = SpringForce.STIFFNESS_LOW,
        dampingRatio: Float = 0.5f,
        onEnd: (() -> Unit)? = null,
        callback: (value: Float) -> Unit
    ) = SpringAnimation(FloatValueHolder()).apply {
        setStartValue(startValue)
        spring = SpringForce(finalValue).apply {
            this.stiffness = stiffness
            this.dampingRatio = dampingRatio
        }
        addUpdateListener { _, value, _ ->
            callback(value)
        }
        addEndListener { _, _, _, _ ->
            onEnd?.invoke()
        }
        start()
    }
}