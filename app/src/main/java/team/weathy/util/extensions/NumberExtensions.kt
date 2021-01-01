package team.weathy.util.extensions

import androidx.core.math.MathUtils


fun Int.clamp(min: Int, max: Int) = MathUtils.clamp(this, min, max)
fun Float.clamp(min: Float, max: Float) = MathUtils.clamp(this, min, max)