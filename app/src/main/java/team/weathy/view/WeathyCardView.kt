package team.weathy.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.res.use
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import team.weathy.R
import team.weathy.util.OnChangeProp
import team.weathy.util.dpFloat
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.setShadowColorIfAvailable
import team.weathy.util.extensions.toPixel

class WeathyCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    private val defaultShadowColor = Color.BLACK

    private var radius by OnChangeProp(35.dpFloat) {
        updateUI()
    }
    private var shadowColor by OnChangeProp(defaultShadowColor) {
        updateUI()
    }
    private var disableShadow by OnChangeProp(false) {
        updateUI()
    }
    private var strokeColor by OnChangeProp(Color.TRANSPARENT) {
        updateUI()
    }
    private var strokeWidth by OnChangeProp(0f) {
        updateUI()
    }

    init {
        if (attrs != null) {
            getStyleableAttrs(attrs)
        }

        updateUI()
    }

    private fun getStyleableAttrs(attr: AttributeSet) {
        context.theme.obtainStyledAttributes(attr, R.styleable.WeathyCardView, 0, 0).use { arr ->
            radius = arr.getDimension(R.styleable.WeathyCardView_weathy_radius, 35.dpFloat)
            shadowColor = arr.getColor(R.styleable.WeathyCardView_weathy_shadow_color, defaultShadowColor)
            disableShadow = arr.getBoolean(R.styleable.WeathyCardView_weathy_disable_shadow, false)
            strokeColor = arr.getColor(R.styleable.WeathyCardView_weathy_stroke_color, Color.TRANSPARENT)
            strokeWidth = arr.getDimension(R.styleable.WeathyCardView_weathy_stroke_width, 0f)
        }
    }

    private fun updateUI() {
        background = MaterialShapeDrawable(ShapeAppearanceModel().withCornerSize(radius)).apply {
            fillColor = ColorStateList.valueOf(getColor(R.color.white))
            strokeWidth = this@WeathyCardView.strokeWidth
            strokeColor = ColorStateList.valueOf(this@WeathyCardView.strokeColor)
        }
        setShadowColorIfAvailable(shadowColor)

        elevation = if (disableShadow) 0f else toPixel(8).toFloat()


        invalidate()
    }
}