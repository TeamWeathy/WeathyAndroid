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
import team.weathy.util.extensions.px
import team.weathy.util.extensions.setShadowColorIfAvailable

class WeathyCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    private val defaultShadowColor = Color.BLACK

    var radius by OnChangeProp(35.dpFloat) {
        updateUI()
    }
    var shadowColor by OnChangeProp(defaultShadowColor) {
        updateUI()
    }
    var disableShadow by OnChangeProp(false) {
        updateUI()
    }
    var strokeColor by OnChangeProp(Color.TRANSPARENT) {
        updateUI()
    }
    var strokeWidth by OnChangeProp(0f) {
        updateUI()
    }
    var cardBackgroundColor by OnChangeProp(Color.WHITE) {
        updateUI()
    }

    init {
        if (attrs != null) {
            getStyleableAttrs(attrs)
        }
        elevation = if (disableShadow) 0f else px(8).toFloat()
        updateUI()
    }

    private fun getStyleableAttrs(attr: AttributeSet) {
        context.theme.obtainStyledAttributes(attr, R.styleable.WeathyCardView, 0, 0).use { arr ->
            radius = arr.getDimension(R.styleable.WeathyCardView_weathy_radius, 35.dpFloat)
            shadowColor = arr.getColor(R.styleable.WeathyCardView_weathy_shadow_color, defaultShadowColor)
            disableShadow = arr.getBoolean(R.styleable.WeathyCardView_weathy_disable_shadow, false)
            strokeColor = arr.getColor(R.styleable.WeathyCardView_weathy_stroke_color, Color.TRANSPARENT)
            strokeWidth = arr.getDimension(R.styleable.WeathyCardView_weathy_stroke_width, 0f)
            cardBackgroundColor = arr.getColor(R.styleable.WeathyCardView_weathy_background_color, Color.WHITE)
        }
    }

    private fun updateUI() {
        background = MaterialShapeDrawable(ShapeAppearanceModel().withCornerSize(radius)).apply {
            fillColor = ColorStateList.valueOf(cardBackgroundColor)
            strokeWidth = this@WeathyCardView.strokeWidth
            strokeColor = ColorStateList.valueOf(this@WeathyCardView.strokeColor)
        }
        setShadowColorIfAvailable(shadowColor)
    }
}