package team.weathy.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
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
import team.weathy.databinding.ViewCalendarItemBinding
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
    private val weekTextGenerator: (text: String) -> View = { text ->
        TextView(context).apply {
            id = ViewCompat.generateViewId()
            textSize = 13f
            setTextColor(
                when (text) {
                    "토" -> getColor(R.color.blue_temp)
                    "일" -> getColor(R.color.red_temp)
                    else -> getColor(R.color.main_grey)
                }
            )
            setText(text)
            gravity = Gravity.CENTER
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
    private val calendarItemGenerator: (viewGroup: ViewGroup, index: Int) -> ViewCalendarItemBinding =
        { viewGroup, index ->
            ViewCalendarItemBinding.bind(
                LayoutInflater.from(context).inflate(R.layout.view_calendar_item, viewGroup, false)
            ).apply {
                day.setTextColor(
                    getColor(
                        when (index) {
                            5 -> R.color.blue_temp
                            6 -> R.color.red_temp
                            else -> R.color.main_grey
                        }
                    )
                )
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

        val topDivider = dividerGenerator().also {
            addView(it, LayoutParams(MATCH_PARENT, px(1)))
            it.updateLayoutParams<LayoutParams> {
                topToBottom = dateText.id
                topMargin = px(16)
            }
        }

        val weekLinearLayout = LinearLayout(context).apply {
            id = ViewCompat.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            weightSum = 7f
        }
        addView(weekLinearLayout, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        weekLinearLayout.updateLayoutParams<LayoutParams> {
            topToBottom = topDivider.id
            topMargin = px(19)
        }
        listOf("월", "화", "수", "목", "금", "토", "일").forEach {
            weekLinearLayout.addView(
                weekTextGenerator(it), LinearLayout.LayoutParams(
                    MATCH_PARENT, WRAP_CONTENT, 1f
                )
            )
        }

        addView(scrollView, LayoutParams(MATCH_PARENT, 0))
        scrollView.updateLayoutParams<LayoutParams> {
            topToBottom = weekLinearLayout.id
            bottomToBottom = parentId
            bottomMargin = px(64)
        }


        scrollView.addView(outerLinearLayout, LayoutParams(MATCH_PARENT, WRAP_CONTENT))


        innerLinearLayout.forEachIndexed { index, inner ->
            if (index != 0) outerLinearLayout.addView(
                dividerGenerator(), LinearLayout.LayoutParams(MATCH_PARENT, px(1), 0f)
            )
            outerLinearLayout.addView(inner, LinearLayout.LayoutParams(MATCH_PARENT, px(104), 0f))

            repeat(7) {
                inner.addView(calendarItemGenerator(inner, it).root)
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
        val collapsed = px(MIN_HEIGHT_DP)
        val expanded = screenHeight - px(EXPAND_MARGIN_BOTTOM_DP)

        updateLayoutParams<ViewGroup.LayoutParams> {

            height =
                MathUtils.clamp((if (startFromCollasped) collapsed else expanded) + movedY.toInt(), collapsed, expanded)
        }
    }

    private fun collapse() {
        SpringAnimation(FloatValueHolder()).apply {
            setStartValue(height.toFloat())
            setMinValue(pxFloat(MIN_HEIGHT_DP))
            spring = SpringForce(pxFloat(MIN_HEIGHT_DP)).apply {
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
            spring = SpringForce(screenHeight - pxFloat(EXPAND_MARGIN_BOTTOM_DP)).apply {
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
        private const val MIN_HEIGHT_DP = 220
        private const val EXPAND_MARGIN_BOTTOM_DP = 120
    }
}