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
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.math.MathUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import team.weathy.R
import team.weathy.util.AnimUtil
import team.weathy.util.OnChangeProp
import team.weathy.util.Once
import team.weathy.util.convertDateToMonthlyIndex
import team.weathy.util.convertDateToWeeklyIndex
import team.weathy.util.convertMonthlyIndexToDate
import team.weathy.util.convertWeeklyIndexToDate
import team.weathy.util.extensions.clamp
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.extensions.pxFloat
import team.weathy.util.extensions.screenHeight
import team.weathy.util.weekOfMonth
import team.weathy.view.calendar.CalendarView.OnDateChangeListener
import java.time.LocalDate
import java.util.*

class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val today = LocalDate.now()

    var curDate: LocalDate by OnChangeProp(LocalDate.now()) {
        updateUIWithCurDate()
        onDateChangeListener?.onChange(it)
        invalidate()
    }
    var onDateChangeListener: OnDateChangeListener? = null

    private val isTodayInCurrentMonth
        get() = curDate.year == today.year && curDate.month == today.month
    private val isTodayInCurrentWeek
        get() = isTodayInCurrentMonth && curDate.weekOfMonth == today.weekOfMonth

    private fun isIncludedInTodayWeek(idx: Int) =
        isTodayInCurrentMonth && isTodayInCurrentWeek && (today.dayOfWeek.value - 1) == idx % 7

    private var isExpanded by OnChangeProp(false) { expanded ->
        if (expanded) {
            enableScroll()
            enableTouchMonthlyPagerOnly()
        } else {
            disableScroll()
            enableTouchWeeklyPagerOnly()
        }
        AnimUtil.runSpringAnimation(animValue, if (expanded) 1f else 0f, 500f) {
            animValue = it
        }

        scrollToTop()
        invalidate()
    }

    private val animLiveData = MutableLiveData(0f)
    private var animValue by OnChangeProp(0f) {
        adjustUIsWithAnimValue()
        animLiveData.value = it
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


    private val viewPagerLayoutParams = {
        LayoutParams(MATCH_PARENT, 0).apply {
            topToBottom = weekTextLayout.id
            bottomToBottom = parentId
            bottomMargin = px(32)
        }
    }

    private val monthlyViewPager = ViewPager2(context).apply {
        layoutParams = viewPagerLayoutParams()

        adapter = MonthlyAdapter(animLiveData, scrollEnabled, onScrollToTop)
        setCurrentItem(MonthlyAdapter.MAX_ITEM_COUNT, false)
        alpha = 0f

        registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val newDate = convertMonthlyIndexToDate(position).withDayOfMonth(1)
                if (isExpanded && curDate != newDate) {
                    curDate = newDate
                }
            }
        })

        offscreenPageLimit = 1
    }
    private val weeklyViewPager = ViewPager2(context).apply {
        layoutParams = viewPagerLayoutParams()

        adapter = WeeklyAdapter(animLiveData)
        setCurrentItem(WeeklyAdapter.MAX_ITEM_COUNT, false)

        //        setPageTransformer { page, position ->
        //            page.pivotX = if (position < 0) page.width.toFloat() else 0f
        //            page.pivotY = page.height * 0.5f
        //            page.rotationY = 20f * position
        //        }

        registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val newDate = convertWeeklyIndexToDate(position)
                if (!isExpanded && curDate != newDate) {
                    curDate = newDate
                }
            }
        })

        offscreenPageLimit = 1
    }

    init {
        initContainer()
        addViews()
        configureExpandGestureHandling()
        updateUIWithCurDate()
        enableTouchWeeklyPagerOnly()
        adjustWeekTextColors()
    }

    private fun updateUIWithCurDate() {
        dateText.text = "${curDate.year} .${curDate.monthValue.toString().padStart(2, '0')}"

        if (!isExpanded) {
            val nextIdx = convertDateToMonthlyIndex(curDate)

            if (monthlyViewPager.currentItem != nextIdx) {
                monthlyViewPager.setCurrentItem(
                    nextIdx, false
                )
            }
        } else {
            val nextIdx = convertDateToWeeklyIndex(curDate)

            if (weeklyViewPager.currentItem != nextIdx) {
                weeklyViewPager.setCurrentItem(
                    nextIdx, false
                )
            }
        }

        adjustWeekTextColors()
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
        addViewPagers()
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

    private fun addViewPagers() {
        addView(monthlyViewPager)
        addView(weeklyViewPager)
    }

    private fun getWeekTextColor(@IntRange(from = 0L, to = 6L) week: Int, includeToday: Boolean = false): Int {
        val weekColor = getColor(CalendarUtil.getColorFromWeek(week))
        if (!includeToday) return weekColor

        return ColorUtils.blendARGB(Color.WHITE, weekColor, animValue.clamp(0f, 1f))
    }

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

        if (isTodayInCurrentMonth) {
            val widthWithoutPadding = width - paddingHorizontal * 2f
            val rawWidth = widthWithoutPadding / 7f
            val maxWidth = pxFloat(42)
            val capsuleWidth = rawWidth.coerceAtMost(maxWidth)
            val capsuleLeftPadding = if (rawWidth >= maxWidth) (rawWidth - maxWidth) / 2f else 0f
            val capsuleHeight = pxFloat(64)
            val capsuleLeft = paddingHorizontal + capsuleLeftPadding + (today.dayOfWeek.value - 1) * rawWidth
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
    }


    private var expandVelocityTracker: VelocityTracker? = null
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
                    isExpanded = expandVelocityTracker!!.yVelocity > 0
                }
            }
            true
        }
    }

    private fun adjustUIsWithAnimValue() {
        adjustHeight()
        adjustWeekTextColors()
        adjustWeekCapsulePaintOpacity()
        adjustViewPagersOpacity()
    }

    private fun adjustHeight() = updateLayoutParams<ViewGroup.LayoutParams> {
        height = MathUtils.lerp(collapsed.toFloat(), expanded.toFloat(), animValue).toInt()
    }

    private fun adjustWeekTextColors() {
        weekTexts.forEachIndexed { idx, textView ->
            textView.setTextColor(getWeekTextColor(idx, isIncludedInTodayWeek(idx)))
        }
    }

    private fun adjustWeekCapsulePaintOpacity() {
        weekCapsulePaint.alpha = (255 - animValue * 255).toInt().clamp(0, 255)
    }

    private fun adjustViewPagersOpacity() {
        weeklyViewPager.alpha = 1 - animValue
        monthlyViewPager.alpha = animValue
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

    private fun enableTouchWeeklyPagerOnly() {
        weeklyViewPager.isUserInputEnabled = true
        monthlyViewPager.isUserInputEnabled = false
    }

    private fun enableTouchMonthlyPagerOnly() {
        weeklyViewPager.isUserInputEnabled = false
        monthlyViewPager.isUserInputEnabled = true
    }

    fun interface OnDateChangeListener {
        fun onChange(date: LocalDate)
    }

    companion object {
        private const val parentId = ConstraintSet.PARENT_ID
        private const val MIN_HEIGHT_DP = 220
        private const val EXPAND_MARGIN_BOTTOM_DP = 120
    }
}

@BindingAdapter("curDate")
fun CalendarView.setCurDate(date: LocalDate) {
    if (curDate != date) curDate = date
}

@InverseBindingAdapter(attribute = "curDate")
fun CalendarView.getCurDate(): LocalDate = curDate

@BindingAdapter("curDateAttrChanged")
fun CalendarView.setListener(attrChange: InverseBindingListener) {
    onDateChangeListener = OnDateChangeListener {
        attrChange.onChange()
    }
}
