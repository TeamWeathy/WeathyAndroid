package team.weathy.view

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable.ShaderFactory
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View
import team.weathy.R
import team.weathy.util.dpFloat
import kotlin.math.roundToInt


class RecordFab @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val bgPaint = PaintDrawable()
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.argb((255 * 0.8f).roundToInt(), 129, 226, 210)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    init {


        val shaderFactory: ShaderFactory = object : ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(
                    0f, 0f, 0f, height.toFloat(), intArrayOf(
                        Color.argb(0, 129, 226, 210),
                        Color.argb((0.39f * 255).roundToInt(), 129, 226, 210),
                        Color.argb((0.95f * 255).roundToInt(), 129, 226, 210),
                        Color.argb((0.68f * 255).roundToInt(), 129, 226, 210),
                        Color.argb((0.28f * 255).roundToInt(), 129, 226, 210),
                    ), floatArrayOf(
                        0.06f, 0.29f, 0.51f, 0.76f, 1.0f
                    ), CLAMP
                )
            }
        }
        bgPaint.shape = OvalShape()
        bgPaint.shaderFactory = shaderFactory

        background = bgPaint


        isClickable = true
        isFocusable = true

        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.mtrl_btn_state_list_anim)

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(width / 2f, height / 2f, width / 2f - 1.dpFloat, strokePaint)
    }

}