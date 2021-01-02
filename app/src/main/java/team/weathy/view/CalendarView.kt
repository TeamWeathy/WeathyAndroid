package team.weathy.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
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
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.math.MathUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import team.weathy.R
import team.weathy.databinding.ViewCalendarItemBinding
import team.weathy.util.AnimUtil
import team.weathy.util.OnChangeProp
import team.weathy.util.dpFloat
import team.weathy.util.extensions.clamp
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.extensions.pxFloat
import team.weathy.util.extensions.screenHeight

class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    var today by OnChangeProp(1) {
        lineIndexIncludesToday = (it - 1) / 7
        updateUIWithToday()
    }
    private var lineIndexIncludesToday = 0

    private fun isIncludedInTodayWeek(idx: Int) = (today - 1) % 7 == idx % 7
    private var animValue by OnChangeProp(0f) {
        adjustUIsWithAnimValue()
    }


    private val collapsed
        get() = px(MIN_HEIGHT_DP)
    private val expanded
        get() = screenHeight - px(EXPAND_MARGIN_BOTTOM_DP)

    private val paddingHorizontal = px(24)

    private val dateText = TextView(context).apply {
        id = ViewCompat.generateViewId()
        textSize = 25f
        if (!isInEditMode) typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        text = "2020.12"
        setTextColor(getColor(R.color.main_grey))
        gravity = Gravity.CENTER
    }
    private val dividerGenerator = {
        View(context).apply {
            id = ViewCompat.generateViewId()
            setBackgroundColor(getColor(R.color.sub_grey_5))
        }
    }
    private val weekTextLayout = LinearLayout(context).apply {
        id = ViewCompat.generateViewId()
        orientation = LinearLayout.HORIZONTAL
        weightSum = 7f
    }
    private val weekTexts = (0..6).map {
        TextView(context).apply {
            id = ViewCompat.generateViewId()
            textSize = 13f
            text = listOf("월", "화", "수", "목", "금", "토", "일")[it]
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

    private val calendarItems = (0..34).map {
        ViewCalendarItemBinding.inflate(LayoutInflater.from(context), null, false).apply {
            root.id = ViewCompat.generateViewId()

            day.text = (it + 1).toString()
        }
    }

    private val notchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
    }
    private val weekCapsulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
        setShadowLayer(12f, 0f, 0f, getColor(R.color.main_mint))
    }

    init {
        initContainer()
        addViews()
        configureTouch()
        updateUIWithToday()
        isSaveEnabled = true
    }

    private fun initContainer() {
        setPadding(paddingHorizontal, 0, paddingHorizontal, 0)

        background = MaterialShapeDrawable(
            ShapeAppearanceModel().toBuilder().setBottomLeftCorner(CornerFamily.ROUNDED, px(35).toFloat())
                .setBottomRightCorner(CornerFamily.ROUNDED, px(35).toFloat()).build()
        ).apply {
            fillColor = ColorStateList.valueOf(getColor(R.color.white))
        }
        elevation = px(4).toFloat()
    }

    private fun addViews() {
        addDateText()
        addWeekLayoutAndWeekTexts(addTopDivider())
        addScrollView(weekTextLayout)
        addCalendarItems()
    }

    private fun addDateText() {
        addView(dateText, LayoutParams(0, WRAP_CONTENT))
        dateText.updateLayoutParams<LayoutParams> {
            topToTop = parentId
            leftToLeft = parentId
            rightToRight = parentId
            topMargin = px(16)
        }
    }

    private fun addTopDivider() = dividerGenerator().also {
        addView(it, LayoutParams(MATCH_PARENT, px(1)))
        it.updateLayoutParams<LayoutParams> {
            topToBottom = dateText.id
            topMargin = px(16)
        }
    }

    private fun addWeekLayoutAndWeekTexts(topDivider: View) = weekTextLayout.run {
        this@CalendarView.addView(this, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

        updateLayoutParams<LayoutParams> {
            topToBottom = topDivider.id
            topMargin = px(20)
        }

        weekTexts.forEach {
            addView(
                it, LinearLayout.LayoutParams(
                    MATCH_PARENT, WRAP_CONTENT, 1f
                )
            )
        }
    }

    private fun addScrollView(weekLayout: View) {
        addView(scrollView, LayoutParams(MATCH_PARENT, 0))
        scrollView.updateLayoutParams<LayoutParams> {
            topToBottom = weekLayout.id
            bottomToBottom = parentId
            bottomMargin = px(32)
        }
    }

    private fun addCalendarItems() {
        scrollView.addView(outerLinearLayout, LayoutParams(MATCH_PARENT, WRAP_CONTENT))

        innerLinearLayout.forEachIndexed { index, inner ->
            if (index != 0) outerLinearLayout.addView(
                dividerGenerator(), LinearLayout.LayoutParams(MATCH_PARENT, px(1), 0f)
            )
            outerLinearLayout.addView(inner, LinearLayout.LayoutParams(MATCH_PARENT, px(104), 0f))

            calendarItems.subList(index * 7, index * 7 + 7).forEach {
                inner.addView(it.root, LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 1f))
            }
        }
    }

    private fun updateUIWithToday() {
        weekTexts.forEachIndexed { idx, textView ->
            textView.setTextColor(getWeekTextColor(idx, isIncludedInTodayWeek(idx)))
        }

        calendarItems.forEachIndexed { idx, binding ->
            val isToday = idx + 1 == today
            binding.circleSmall.isVisible = isToday
            binding.day.setTextColor(getDayTextColor(idx % 7, isToday))
            binding.dayFirstLine.setTextColor(getDayTextColor(idx % 7, isIncludedInTodayWeek(idx)))
            binding.dayFirstLine.text = (idx % 7 + 1 + lineIndexIncludesToday * 7).toString()
        }
    }


    private fun getWeekTextColor(@IntRange(from = 0L, to = 6L) week: Int, includeToday: Boolean = false): Int {
        val weekColor = getColorFromWeek(week)
        if (!includeToday) return weekColor

        return ColorUtils.blendARGB(Color.WHITE, weekColor, animValue.clamp(0f, 1f))
    }

    private fun getDayTextColor(@IntRange(from = 0L, to = 6L) week: Int, isToday: Boolean = false): Int {
        if (isToday) return Color.WHITE

        return getColorFromWeek(week)
    }

    private fun getColorFromWeek(@IntRange(from = 0L, to = 6L) week: Int) = getColor(
        when (week % 7) {
            5 -> R.color.blue_temp
            6 -> R.color.red_temp
            else -> R.color.main_grey
        }
    )


    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            width / 2f - px(30),
            height - pxFloat(16),
            width / 2f + px(30),
            height - pxFloat(11),
            pxFloat(10),
            pxFloat(10),
            notchPaint,
        )

        val widthWithoutPadding = width - paddingHorizontal * 2f
        val rawWidth = widthWithoutPadding / 7f
        val maxWidth = pxFloat(42)
        val capsuleWidth = rawWidth.coerceAtMost(maxWidth)
        val capsuleLeftPadding = if (rawWidth >= maxWidth) (rawWidth - maxWidth) / 2f else 0f
        val capsuleHeight = pxFloat(64)
        val capsuleLeft = paddingHorizontal + capsuleLeftPadding + ((today - 1) % 7) * rawWidth
        val capsuleWidthRadius = capsuleWidth / 2f

        canvas.drawRoundRect(
            capsuleLeft,
            pxFloat(72),
            capsuleLeft + capsuleWidth,
            pxFloat(72) + capsuleHeight,
            capsuleWidthRadius,
            capsuleWidthRadius,
            weekCapsulePaint,
        )
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

                    val curHeight = event.y
                    animValue = ((curHeight - collapsed) / (expanded - collapsed)).clamp(0f, 1.2f)
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

    private fun adjustUIsWithAnimValue() {
        adjustHeight()
        adjustWeekTexts()
        adjustCalendarItems()
        adjustWeekCapsulePaintOpacity()
    }

    private fun adjustHeight() = updateLayoutParams<ViewGroup.LayoutParams> {
        height = MathUtils.lerp(collapsed.toFloat(), expanded.toFloat(), animValue).toInt()
    }

    private fun adjustWeekTexts() {
        weekTexts.forEachIndexed { idx, textView ->
            textView.setTextColor(getWeekTextColor(idx, isIncludedInTodayWeek(idx)))
        }
    }

    private fun adjustCalendarItems() {
        calendarItems.forEach { binding ->
            binding.tempHigh.alpha = animValue
            binding.tempLow.alpha = animValue
            binding.circle.alpha = 1 - animValue

            listOf(binding.day, binding.dayFirstLine).forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, MathUtils.lerp(18f, 16f, animValue))
                it.updateLayoutParams<LayoutParams> {
                    topMargin = MathUtils.lerp(5.dpFloat, 18.dpFloat, animValue).toInt()
                }
            }

            binding.day.alpha = animValue
            binding.dayFirstLine.alpha = 1 - animValue
        }

        val itemsExceptFirstLine = calendarItems.subList(7, calendarItems.size)
        itemsExceptFirstLine.forEachIndexed { index, binding ->
            binding.root.alpha = animValue
            binding.root.translationY = MathUtils.lerp(index * 4f, 0f, animValue)
        }

        val itemToday = calendarItems[today - 1]
        itemToday.run {
            circleSmall.alpha = animValue
            circleSmall.scaleX = (animValue + 0.3f).clamp(0.5f, 1.0f)
            circleSmall.scaleY = (animValue + 0.3f).clamp(0.5f, 1.0f)
        }
    }

    private fun adjustWeekCapsulePaintOpacity() {
        weekCapsulePaint.alpha = (255 - animValue * 255).toInt().clamp(0, 255)
    }

    private fun collapse() {
        disableScroll()
        scrollToTop()
        AnimUtil.runSpringAnimation(animValue, 0f, 500f) {
            animValue = it
        }
    }

    private fun expand() {
        enableScroll()
        scrollToTop()
        AnimUtil.runSpringAnimation(animValue, 1f, 500f) {
            animValue = it
        }
    }

    private fun disableScroll() {
        scrollView.setOnTouchListener { _, _ -> true }
    }

    private fun enableScroll() {
        scrollView.setOnTouchListener(null)
    }

    private fun scrollToTop() {
        scrollView.smoothScrollTo(0, 0)
    }

    companion object {
        private const val parentId = ConstraintSet.PARENT_ID
        private const val MIN_HEIGHT_DP = 220
        private const val EXPAND_MARGIN_BOTTOM_DP = 120
    }
}