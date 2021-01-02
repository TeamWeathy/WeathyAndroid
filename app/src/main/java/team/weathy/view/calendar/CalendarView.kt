package team.weathy.view.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.math.MathUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import team.weathy.R
import team.weathy.util.AnimUtil
import team.weathy.util.OnChangeProp
import team.weathy.util.OnLiveDataProp
import team.weathy.util.Once
import team.weathy.util.extensions.clamp
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.extensions.pxFloat
import team.weathy.util.extensions.screenHeight

class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    var today by OnChangeProp(1) {
        updateUIWithToday()
    }

    private fun isIncludedInTodayWeek(idx: Int) = (today - 1) % 7 == idx % 7

    private lateinit var animLiveData: LiveData<Float>
    private var animValue by OnLiveDataProp(0f) {
        animLiveData = it
    }
    private val animValueObserver = Observer<Float> {
        adjustUIsWithAnimValue()
    }

    private val scrollEnabled = MutableLiveData(false)
    private val onScrollToTop = MutableLiveData<Once<Unit>>()

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
    private val topDivider = View(context).apply {
        id = ViewCompat.generateViewId()
        setBackgroundColor(getColor(R.color.sub_grey_5))

        layoutParams = LayoutParams(MATCH_PARENT, px(1)).apply {
            topToBottom = dateText.id
            topMargin = px(16)
        }
    }

    private val weekTextLayout = LinearLayout(context).apply {
        id = ViewCompat.generateViewId()
        orientation = LinearLayout.HORIZONTAL
        weightSum = 7f

        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topToBottom = topDivider.id
            topMargin = px(20)
        }
    }
    private val weekTexts = (0..6).map {
        TextView(context).apply {
            id = ViewCompat.generateViewId()
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
            text = listOf("일", "월", "화", "수", "목", "금", "토")[it]
            gravity = Gravity.CENTER

            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f)
        }
    }
    private val viewPager = ViewPager2(context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, 0).apply {
            topToBottom = weekTextLayout.id
            bottomToBottom = parentId
            bottomMargin = px(32)
        }

        adapter = MonthlyAdapter(animLiveData, scrollEnabled, onScrollToTop).apply {
            submitList(listOf(1, 2, 3, 4, 5))
        }

        setPageTransformer { page, position ->
            page.pivotX = if (position < 0) page.width.toFloat() else 0f
            page.pivotY = page.height * 0.5f
            page.rotationY = 50f * position
        }
    }

    init {
        initContainer()
        addViews()
        configureExpandGestureHandling()
        updateUIWithToday()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animLiveData.observeForever(animValueObserver)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animLiveData.removeObserver(animValueObserver)
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
        addTopDivider()
        addWeekLayoutAndWeekTexts()
        addViewPager()
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

    private fun addTopDivider() = addView(topDivider)

    private fun addWeekLayoutAndWeekTexts() = weekTextLayout.also { layout ->
        addView(layout)
        weekTexts.forEach(layout::addView)
    }

    private fun addViewPager() = addView(viewPager)

    private fun updateUIWithToday() {
        weekTexts.forEachIndexed { idx, textView ->
            textView.setTextColor(getWeekTextColor(idx, isIncludedInTodayWeek(idx)))
        }
    }

    private fun getWeekTextColor(@IntRange(from = 0L, to = 6L) week: Int, includeToday: Boolean = false): Int {
        val weekColor = getColorFromWeek(week)
        if (!includeToday) return weekColor

        return ColorUtils.blendARGB(Color.WHITE, weekColor, animValue.clamp(0f, 1f))
    }

    private fun getColorFromWeek(@IntRange(from = 0L, to = 6L) week: Int) = getColor(
        when (week % 7) {
            6 -> R.color.blue_temp
            0 -> R.color.red_temp
            else -> R.color.main_grey
        }
    )


    private val notchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
    }
    private val weekCapsulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
        setShadowLayer(12f, 0f, 0f, getColor(R.color.main_mint))
    }
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


    private var expandVelocityTracker: VelocityTracker? = null
    private var startFromCollasped = true
    private var offsetY = 0f

    @SuppressLint("Recycle")
    private fun configureExpandGestureHandling() {
        setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.y <= view.height - px(30)) {
                        return@setOnTouchListener false
                    }

                    expandVelocityTracker?.clear()
                    expandVelocityTracker = expandVelocityTracker ?: VelocityTracker.obtain()
                    expandVelocityTracker?.addMovement(event)

                    offsetY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    expandVelocityTracker?.apply {
                        addMovement(event)
                        computeCurrentVelocity(1000)
                    }

                    animValue = ((event.y - collapsed) / (expanded - collapsed)).clamp(0f, 1.2f)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    startFromCollasped = if (expandVelocityTracker!!.yVelocity > 0) {
                        expand()
                        false
                    } else {
                        collapse()
                        true
                    }
                }
            }
            true
        }
    }

    private fun adjustUIsWithAnimValue() {
        adjustHeight()
        adjustWeekTexts()
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
        scrollEnabled.value = false
    }

    private fun enableScroll() {
        scrollEnabled.value = true
    }

    private fun scrollToTop() {
        onScrollToTop.value = Once(Unit)
    }

    companion object {
        private const val parentId = ConstraintSet.PARENT_ID
        private const val MIN_HEIGHT_DP = 220
        private const val EXPAND_MARGIN_BOTTOM_DP = 120
    }
}