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

class WeathyCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    private val defaultShadowColor = Color.BLACK

    private var radius by OnChangeProp(35.dpFloat) {
        updateUI()
    }
    private var shadowColor by OnChangeProp(defaultShadowColor) {
        updateUI()
    }

    init {
        if (attrs != null) {
            getStyleableAttrs(attrs)
        }

        initUI()
        updateUI()
    }

    private fun getStyleableAttrs(attr: AttributeSet) {
        context.theme.obtainStyledAttributes(attr, R.styleable.WeathyCardView, 0, 0).use { arr ->
            radius = arr.getDimension(R.styleable.WeathyCardView_weathy_radius, 35.dpFloat)
            shadowColor = arr.getColor(R.styleable.WeathyCardView_weathy_shadow_color, defaultShadowColor)
        }
    }

    private fun initUI() {
        elevation = 8f
    }

    private fun updateUI() {
        background = MaterialShapeDrawable(ShapeAppearanceModel().withCornerSize(radius)).apply {
            fillColor = ColorStateList.valueOf(getColor(R.color.white))
        }
        setShadowColorIfAvailable(shadowColor)

        invalidate()
    }
}