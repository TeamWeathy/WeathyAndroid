package team.weathy.view.calendar

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
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
import android.widget.ImageButton
import android.widget.ImageView.ScaleType.FIT_CENTER
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.math.MathUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import team.weathy.R
import team.weathy.util.AnimUtil
import team.weathy.util.OnChangeProp
import team.weathy.util.SimpleEventLiveData
import team.weathy.util.convertDateToMonthlyIndex
import team.weathy.util.convertDateToWeeklyIndex
import team.weathy.util.convertMonthlyIndexToDate
import team.weathy.util.convertWeeklyIndexToDate
import team.weathy.util.dayOfWeekIndex
import team.weathy.util.emit
import team.weathy.util.extensions.clamp
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.extensions.pxFloat
import team.weathy.util.extensions.screenHeight
import team.weathy.util.setOnDebounceClickListener
import team.weathy.util.weekOfMonth
import team.weathy.view.calendar.CalendarView.OnDateChangeListener
import java.time.LocalDate
import java.util.*

class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {
    private val today = LocalDate.now()

    var curDate: LocalDate by OnChangeProp(LocalDate.now()) {
        onCurDateChanged()
    }
    var onDateChangeListener: OnDateChangeListener? = null

    private val isTodayInCurrentMonth
        get() = curDate.year == today.year && curDate.month == today.month
    private val isTodayInCurrentWeek
        get() = isTodayInCurrentMonth && curDate.weekOfMonth == today.weekOfMonth


    private val animLiveData = MutableLiveData(0f)
    private var animValue by OnChangeProp(0f) {
        animLiveData.value = it
        onAnimValueChanged()
    }

    private val scrollEnabled = MutableLiveData(false)
    private val onScrollToTop = SimpleEventLiveData()

    private val collapsedHeight
        get() = px(MIN_HEIGHT_DP)
    private val expandedHeight
        get() = screenHeight - px(EXPAND_MARGIN_BOTTOM_DP)

    private val paddingHorizontal = px(24)

    private val yearMonthText = TextView(context).apply {
        id = ViewCompat.generateViewId()
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25f)
        if (!isInEditMode) typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        setTextColor(getColor(R.color.main_grey))
        gravity = Gravity.CENTER

        layoutParams = LayoutParams(0, WRAP_CONTENT).apply {
            topToTop = parentId
            leftToLeft = parentId
            rightToRight = parentId
            topMargin = px(26)
        }
    }

    private val todayButton = ImageButton(context).apply {
        setImageResource(R.drawable.ic_today)
        scaleType = FIT_CENTER

        val outValue = TypedValue()
        context.theme.resolveAttribute(attr.selectableItemBackgroundBorderless, outValue, true)
        setBackgroundResource(outValue.resourceId)

        setOnDebounceClickListener {
            curDate = today
        }

        layoutParams = LayoutParams(px(32), px(32)).apply {
            setPadding(px(6), px(6), px(6), px(6))
            topToTop = yearMonthText.id
            bottomToBottom = yearMonthText.id
            rightToRight = parentId
            rightMargin = px(0)
        }
    }

    private val topDivider = View(context).apply {
        id = ViewCompat.generateViewId()
        setBackgroundColor(getColor(R.color.sub_grey_5))

        layoutParams = LayoutParams(MATCH_PARENT, px(1)).apply {
            topToBottom = yearMonthText.id
            topMargin = px(11)
        }
    }

    private val weekTextLayout = LinearLayout(context).apply {
        id = ViewCompat.generateViewId()
        orientation = LinearLayout.HORIZONTAL
        weightSum = 7f

        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topToBottom = topDivider.id
            topMargin = px(16)
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

    private var isExpanded = false
    private fun expand() {
        isExpanded = true
        notifyEnableScroll()
        enableTouchMonthlyPagerOnly()

        springAnim = AnimUtil.runSpringAnimation(animValue * 500f, 500f) {
            animValue = it / 500f
        }

        onExpandedChange()
    }

    private fun collapse() {
        isExpanded = false
        notifyDisableScroll()
        enableTouchWeeklyPagerOnly()

        springAnim = AnimUtil.runSpringAnimation(animValue * 500f, 0f) {
            animValue = it / 500f
        }

        onExpandedChange()
    }

    private fun onExpandedChange() {
        notifyScrollToTop()
        invalidate()
    }

    private val monthlyViewPagerGenerator = {
        ViewPager2(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, 0).apply {
                topToBottom = weekTextLayout.id
                bottomToBottom = parentId
                bottomMargin = px(32)
            }

            adapter = MonthlyAdapter(animLiveData, scrollEnabled, onScrollToTop)
            setCurrentItem(MonthlyAdapter.MAX_ITEM_COUNT, false)
            alpha = 0f

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val newDate = convertMonthlyIndexToDate(position)
                    if (isExpanded && curDate != newDate) {
                        curDate = newDate
                    }
                }
            })

            offscreenPageLimit = 1
        }
    }
    private var monthlyViewPager: ViewPager2? = null
    private val weeklyViewPager = ViewPager2(context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, 0).apply {
            topToBottom = weekTextLayout.id
            height = px(WeeklyView.ITEM_HEIGHT_DP)
        }

        adapter = WeeklyAdapter(animLiveData)
        setCurrentItem(WeeklyAdapter.MAX_ITEM_COUNT, false)

        setPageTransformer { page, position ->
            page.pivotX = if (position < 0) page.width.toFloat() else 0f
            page.pivotY = page.height * 0.5f
            page.rotationY = 35f * position
        }

        registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val newDate = convertWeeklyIndexToDate(position)
                if (!isExpanded && curDate != newDate) {
                    curDate = newDate
                }
            }
        })
    }

    init {
        initContainer()
        addViews()
        configureExpandGestureHandling()
        enableTouchWeeklyPagerOnly()
        changeWeekTextsColor()
        onCurDateChanged()
    }


    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var lazyPagerAddJob: Job
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lazyPagerAddJob = scope.launch {
            delay(700L)
            if (monthlyViewPager == null) {
                monthlyViewPager = monthlyViewPagerGenerator()
                addView(monthlyViewPager!!, 0)
                onCurDateChanged()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lazyPagerAddJob.cancel()
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
        addView(yearMonthText)
        addView(todayButton)
        addView(topDivider)
        addWeekLayoutAndWeekTexts()
        addView(weeklyViewPager)
    }

    private fun addWeekLayoutAndWeekTexts() = weekTextLayout.also { layout ->
        addView(layout)
        weekTexts.forEach(layout::addView)
    }

    private fun onCurDateChanged() {
        setYearMonthTextWithDate(curDate)
        selectPagerItemsWithDate(curDate)

        onDateChangeListener?.onChange(curDate)
        changeWeekTextsColor()
        notifyScrollToTop()
        invalidate()
    }

    private fun setYearMonthTextWithDate(date: LocalDate) {
        yearMonthText.text = "${date.year} .${date.monthValue}"
    }

    private fun selectPagerItemsWithDate(date: LocalDate) {
        val nextMonthlyIndex = convertDateToMonthlyIndex(date)
        val nextWeeklyIndex = convertDateToWeeklyIndex(date)

        if (monthlyViewPager?.currentItem != nextMonthlyIndex) {
            monthlyViewPager?.setCurrentItem(
                nextMonthlyIndex, false
            )
        }

        if (weeklyViewPager.currentItem != nextWeeklyIndex) {
            weeklyViewPager.setCurrentItem(
                nextWeeklyIndex, false
            )
        }
    }


    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
    }
    private val capsulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
        setShadowLayer(12f, 0f, 0f, getColor(R.color.main_mint))
    }

    override fun onDraw(canvas: Canvas) {
        // bar
        canvas.drawRoundRect(
            width / 2f - px(30),
            height - pxFloat(16),
            width / 2f + px(30),
            height - pxFloat(11),
            pxFloat(10),
            pxFloat(10),
            barPaint,
        )

        // capsule
        if (isTodayInCurrentWeek) {
            val widthWithoutPadding = width - paddingHorizontal * 2f
            val rawWidth = widthWithoutPadding / 7f
            val maxWidth = pxFloat(42)
            val capsuleWidth = rawWidth.coerceAtMost(maxWidth)
            val capsuleLeftPadding = if (rawWidth >= maxWidth) (rawWidth - maxWidth) / 2f else 0f
            val capsuleHeight = pxFloat(64)
            val capsuleLeft = paddingHorizontal + capsuleLeftPadding + today.dayOfWeekIndex * rawWidth
            val capsuleWidthRadius = capsuleWidth / 2f

            canvas.drawRoundRect(
                capsuleLeft,
                pxFloat(72),
                capsuleLeft + capsuleWidth,
                pxFloat(72) + capsuleHeight,
                capsuleWidthRadius,
                capsuleWidthRadius,
                capsulePaint,
            )
        }
    }


    private var springAnim: SpringAnimation? = null
    private var tracker: VelocityTracker? = null

    @SuppressLint("Recycle")
    private fun configureExpandGestureHandling() {
        setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.y <= view.height - px(30)) {
                        return@setOnTouchListener false
                    }

                    springAnim?.cancel()

                    tracker?.clear()
                    tracker = tracker ?: VelocityTracker.obtain()
                    tracker?.addMovement(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    tracker?.apply {
                        addMovement(event)
                        computeCurrentVelocity(1000)
                    }

                    animValue = ((event.y - collapsedHeight) / (expandedHeight - collapsedHeight)).clamp(0f, 1.2f)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (tracker!!.yVelocity > 0) expand()
                    else collapse()

                    tracker?.also {
                        it.recycle()
                        tracker = null
                    }
                }
            }
            true
        }
    }

    private fun onAnimValueChanged() {
        animateHeight()
        changeWeekTextsColor()
        animCapsulePaintAlpha()
        animPagersAlpha()
    }

    private fun animateHeight() = updateLayoutParams<ViewGroup.LayoutParams> {
        height = MathUtils.lerp(collapsedHeight.toFloat(), expandedHeight.toFloat(), animValue).toInt()
    }

    private fun changeWeekTextsColor() {
        weekTexts.forEachIndexed { idx, textView ->
            textView.setTextColor(
                CalendarUtil.getWeekTextColor(
                    context, idx, animValue, isTodayInCurrentWeek && today.dayOfWeekIndex == idx
                )
            )
        }
    }


    private fun animCapsulePaintAlpha() {
        capsulePaint.alpha = (255 - animValue * 255).toInt().clamp(0, 255)
    }

    private fun animPagersAlpha() {
        weeklyViewPager.alpha = 1 - animValue
        monthlyViewPager?.alpha = animValue
    }

    private fun notifyDisableScroll() {
        scrollEnabled.value = false
    }

    private fun notifyEnableScroll() {
        scrollEnabled.value = true
    }

    private fun notifyScrollToTop() {
        onScrollToTop.emit()
    }

    private fun enableTouchWeeklyPagerOnly() {
        weeklyViewPager.isUserInputEnabled = true
        monthlyViewPager?.isUserInputEnabled = false
    }

    private fun enableTouchMonthlyPagerOnly() {
        weeklyViewPager.isUserInputEnabled = false
        monthlyViewPager?.isUserInputEnabled = true
    }

    fun interface OnDateChangeListener {
        fun onChange(date: LocalDate)
    }

    companion object {
        private const val parentId = ConstraintSet.PARENT_ID
        private const val MIN_HEIGHT_DP = 224
        private const val EXPAND_MARGIN_BOTTOM_DP = 108
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
