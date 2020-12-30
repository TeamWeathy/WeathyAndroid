package team.weathy.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.dynamicanimation.animation.SpringForce.STIFFNESS_LOW
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import team.weathy.R
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.extensions.pxFloat
import team.weathy.util.extensions.screenHeight

class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val paddingHorizontal = px(24)

    private val dateText = TextView(context).apply {
        id = ViewCompat.generateViewId()
        textSize = 25f
        if (!isInEditMode) typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        text = "2020.12"
        setTextColor(getColor(R.color.main_grey))
    }
    private val dividerGenerator = {
        View(context).apply {
            id = ViewCompat.generateViewId()
            setBackgroundColor(getColor(R.color.sub_grey_5))
        }
    }
    private val scrollView = ScrollView(context).apply {
        id = ViewCompat.generateViewId()
        overScrollMode = ScrollView.OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
    }
    private val outerLinearLayout = LinearLayout(context).apply {
        id = ViewCompat.generateViewId()
        orientation = LinearLayout.VERTICAL
        weightSum = 5f
        setBackgroundColor(Color.TRANSPARENT)
    }
    private val innerLinearLayout = (1..5).map {
        LinearLayout(context).apply {
            id = ViewCompat.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            weightSum = 7f
            setBackgroundColor(Color.TRANSPARENT)
        }
    }


    private val notchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
    }

    init {
        initContainer()
        addViews()
        configureTouch()
    }

    private fun initContainer() {
        setPadding(paddingHorizontal, 0, paddingHorizontal, 0)

        background = MaterialShapeDrawable(
            ShapeAppearanceModel().toBuilder().setBottomLeftCorner(CornerFamily.ROUNDED, px(35).toFloat())
                .setBottomRightCorner(CornerFamily.ROUNDED, px(35).toFloat()).build()
        ).apply {
            fillColor = ColorStateList.valueOf(getColor(R.color.white))
        }
        elevation = px(8).toFloat()
    }

    private fun addViews() {
        addView(dateText, LayoutParams(0, WRAP_CONTENT))
        dateText.updateLayoutParams<LayoutParams> {
            topToTop = parentId
            leftToLeft = parentId
            rightToRight = parentId
            topMargin = px(16)
        }

        addView(scrollView, LayoutParams(MATCH_PARENT, 0))
        scrollView.updateLayoutParams<LayoutParams> {
            topToBottom = dateText.id
            topMargin = px(16)
            bottomToBottom = parentId
            bottomMargin = px(64)
        }

        scrollView.addView(outerLinearLayout, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

//        addView(outerLinearLayout, LayoutParams(MATCH_PARENT, 0))
//        outerLinearLayout.updateLayoutParams<LayoutParams> {
//            topToBottom = dateText.id
//            topMargin = px(16)
//            bottomToBottom = parentId
//            bottomMargin = px(64)
//        }

        innerLinearLayout.forEach { inner ->
            outerLinearLayout.addView(dividerGenerator(), LinearLayout.LayoutParams(MATCH_PARENT, px(1), 0f))
            outerLinearLayout.addView(inner, LinearLayout.LayoutParams(MATCH_PARENT, px(100), 0f))

            (1..7).map {
                LayoutInflater.from(context).inflate(R.layout.view_calendar_item, inner as ViewGroup, true)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            width / 2f - px(30),
            height - pxFloat(16),
            width / 2f + px(30),
            height - pxFloat(11),
            pxFloat(10),
            pxFloat(10),
            notchPaint
        )

//        val columnWidth = (width - paddingHorizontal * 2) / 7f

//        canvas.drawRoundRect(
//            paddingHorizontal.toFloat(),
//            pxFloat(70),
//            paddingHorizontal.toFloat() + columnWidth,
//            pxFloat(170),
//            columnWidth / 2,
//            columnWidth / 2,
//            notchPaint
//        )
    }

    private var velocityTracker: VelocityTracker? = null
    private var startFromCollasped = true
    private var offsetY = 0f
    private var movedY = 0f

    @SuppressLint("Recycle")
    private fun configureTouch() {
        setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.y > view.height - px(30)) {
                        velocityTracker?.clear()
                        velocityTracker = velocityTracker ?: VelocityTracker.obtain()
                        velocityTracker?.addMovement(event)
                        offsetY = event.y
                    } else {
                        return@setOnTouchListener false
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    velocityTracker?.apply {
                        addMovement(event)
                        computeCurrentVelocity(1000)
                    }

                    movedY = event.y - offsetY
                    animY()
                }
                MotionEvent.ACTION_UP -> {
                    startFromCollasped = if (velocityTracker!!.yVelocity > 0) {
                        expand()
                        false
                    } else {
                        collapse()
                        true
                    }
                    movedY = 0f

                }
            }
            true
        }
    }

    private fun animY() {
        val collapsed = px(240)
        val expanded = screenHeight - px(120)

        updateLayoutParams<ViewGroup.LayoutParams> {

            height =
                MathUtils.clamp((if (startFromCollasped) collapsed else expanded) + movedY.toInt(), collapsed, expanded)
        }
    }

    private fun collapse() {
        SpringAnimation(FloatValueHolder()).apply {
            setStartValue(height.toFloat())
            setMinValue(pxFloat(240))
            spring = SpringForce(pxFloat(240)).apply {
                stiffness = STIFFNESS_LOW
            }
            addUpdateListener { _, value, _ ->
                updateLayoutParams<ViewGroup.LayoutParams> {
                    height = value.toInt()
                }
            }
        }.start()
    }

    private fun expand() {
        SpringAnimation(FloatValueHolder()).apply {
            setStartValue(height.toFloat())
            spring = SpringForce(screenHeight - pxFloat(120)).apply {
                stiffness = STIFFNESS_LOW
            }
            addUpdateListener { _, value, _ ->
                updateLayoutParams<ViewGroup.LayoutParams> {
                    height = value.toInt()
                }
            }
        }.start()
    }

    companion object {
        private const val parentId = ConstraintSet.PARENT_ID
    }
}