package team.weathy.view

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import team.weathy.R
import team.weathy.util.dpFloat
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.extensions.setShadowColorIfAvailable
import kotlin.math.roundToInt


class RecordFab @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.white)
    }
    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb((255 * 0.8f).roundToInt(), 129, 226, 210)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    init {

        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, view.width / 2f)
            }
        }

        gradientPaint.shader = LinearGradient(
            0f, 0f, 0f, 53.dpFloat, intArrayOf(
                Color.argb(0, 129, 226, 210),
                Color.argb((0.39f * 255).roundToInt(), 129, 226, 210),
                Color.argb((0.95f * 255).roundToInt(), 129, 226, 210),
                Color.argb((0.68f * 255).roundToInt(), 129, 226, 210),
                Color.argb((0.28f * 255).roundToInt(), 129, 226, 210),
            ), floatArrayOf(
                0.06f, 0.29f, 0.51f, 0.76f, 1.0f
            ), Shader.TileMode.CLAMP
        )
        gradientPaint.isDither = true


        isEnabled = true
        isClickable = true
        isFocusable = true

        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.mtrl_btn_state_list_anim)
        setShadowColorIfAvailable(getColor(R.color.main_mint_shadow))
        elevation = 16.dpFloat
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, whitePaint)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, gradientPaint)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, strokePaint)

        canvas.drawRoundRect(
            width / 2f - px(10),
            height / 2f - px(2),
            width / 2f + px(10),
            height / 2f + px(2),
            px(10).toFloat(),
            px(10).toFloat(),
            whitePaint
        )

        canvas.drawRoundRect(
            width / 2f - px(2),
            height / 2f - px(10),
            width / 2f + px(2),
            height / 2f + px(10),
            px(10).toFloat(),
            px(10).toFloat(),
            whitePaint
        )
    }

}