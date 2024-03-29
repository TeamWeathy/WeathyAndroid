package team.weathy.view.calendar

import android.R.attr
import android.animation.AnimatorInflater
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
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ImageView.ScaleType.FIT_CENTER
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.MutableLiveData
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.math.MathUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.*
import team.weathy.R
import team.weathy.model.entity.CalendarPreview
import team.weathy.ui.main.calendar.YearMonthFormat
import team.weathy.util.*
import team.weathy.util.extensions.*
import java.time.LocalDate

class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {
    private val today = LocalDate.now()

    var onDateChangeListener: ((date: LocalDate) -> Unit)? = null
    var onSelectedDateChangeListener: ((date: LocalDate) -> Unit)? = null

    private val curDateLiveData = MutableLiveData(LocalDate.now())
    var curDate: LocalDate by OnChangeProp(LocalDate.now()) {
        onCurDateChanged()
    }

    private val selectedDateLiveData = MutableLiveData(LocalDate.now())
    var selectedDate: LocalDate by OnChangeProp(LocalDate.now()) {
        selectedDateLiveData.value = it
        onSelectedDateChangeListener?.invoke(it)
        curDate = it
        invalidate()
        collapse()
    }
    private var rowCount = 4

    private val dataLiveData =
        MutableLiveData<Map<YearMonthFormat, List<CalendarPreview?>>>(mapOf())
    var data: Map<YearMonthFormat, List<CalendarPreview?>> by OnChangeProp(mapOf()) {
        dataLiveData.value = it
    }

    var onClickYearMonthText: (() -> Unit)? = null

    private val isTodayInCurrentMonth
        get() = curDate.year == today.year && curDate.month == today.month
    private val isTodayInCurrentWeek
        get() = isTodayInCurrentMonth && curDate.weekOfMonth == today.weekOfMonth
    private val isSelectedInCurrentWeek
        get() = selectedDate.year == curDate.year && selectedDate.month == curDate.month && selectedDate.weekOfMonth == curDate.weekOfMonth


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
        stateListAnimator = AnimatorInflater.loadStateListAnimator(
            context,
            R.animator.pressed_alpha_state_list_anim
        )

        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            topToTop = parentId
            leftToLeft = parentId
            rightToRight = parentId
            topMargin = px(26)
        }
        setOnDebounceClickListener {
            onClickYearMonthText?.invoke()
        }
    }

    private val downArrow = ImageView(context).apply {
        id = ViewCompat.generateViewId()
        setImageResource(R.drawable.calendar_btn_arrow)
        scaleType = FIT_CENTER
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            topToTop = yearMonthText.id
            bottomToBottom = yearMonthText.id
            leftToRight = yearMonthText.id
            leftMargin = 4.dp
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
            selectedDate = today
        }

        layoutParams = LayoutParams(px(32), px(32)).apply {
            setPadding(px(6), px(6), px(6), px(6))
            topToTop = yearMonthText.id
            bottomToBottom = yearMonthText.id
            rightToRight = parentId
            rightMargin = px(0)
        }
    }

    private val weekTextLayout = LinearLayout(context).apply {
        id = ViewCompat.generateViewId()
        orientation = LinearLayout.HORIZONTAL
        weightSum = 7f

        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topToBottom = yearMonthText.id
            topMargin = px(28)
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

    private val fragmentViewLifecycleOwner
        get() = findFragment<Fragment>().viewLifecycleOwner

    private val monthlyViewPagerGenerator = {
        ViewPager2(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, 0).apply {
                topToBottom = weekTextLayout.id
                bottomToBottom = parentId
                bottomMargin = px(32)
            }

            adapter = MonthlyAdapter(
                animLiveData,
                scrollEnabled,
                onScrollToTop,
                dataLiveData,
                selectedDateLiveData,
                fragmentViewLifecycleOwner,
            ) {
                if (!it.isFuture()) selectedDate = it
            }
            setCurrentItem(MonthlyAdapter.MAX_ITEM_COUNT, false)
            alpha = 0f

            setPageTransformer { page, position ->
                page.pivotX = if (position < 0) page.width.toFloat() else 0f
                page.pivotY = page.height * 0.5f
                page.rotationY = 25f * position
            }

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val (_, firstDateOfMonth) = convertMonthlyIndexToDateToFirstDateOfMonthCalendar(
                        position
                    )
                    if (isExpanded && curDate != firstDateOfMonth) {
                        curDate = firstDateOfMonth
                    }
                }
            })

            offscreenPageLimit = 1
        }
    }
    private var monthlyViewPager: ViewPager2? = null
    private val weeklyViewPagerGenerator = {
        ViewPager2(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, 0).apply {
                topToBottom = weekTextLayout.id
                height = px(WeeklyView.ITEM_HEIGHT_DP)
            }

            adapter = WeeklyAdapter(animLiveData, dataLiveData, fragmentViewLifecycleOwner) {
                if (!it.isFuture()) selectedDate = it
            }
            setCurrentItem(WeeklyAdapter.MAX_ITEM_COUNT, false)

            setPageTransformer { page, position ->
                page.pivotX = if (position < 0) page.width.toFloat() else 0f
                page.pivotY = page.height * 0.5f
                page.rotationY = 40f * position
            }

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val newDate = convertWeeklyIndexToFirstDateOfWeekCalendar(position)
                    if (!isExpanded && curDate != newDate) {
                        curDate = newDate
                    }
                }
            })

            offscreenPageLimit = 1
        }
    }
    private var weeklyViewPager: ViewPager2? = null

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
            if (weeklyViewPager == null) {
                weeklyViewPager = weeklyViewPagerGenerator()
                TransitionManager.beginDelayedTransition(this@CalendarView)
                addView(weeklyViewPager!!, 0)
            }
            delay(400)
            if (monthlyViewPager == null) {
                monthlyViewPager = monthlyViewPagerGenerator()
                addView(monthlyViewPager!!, 0)
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
            ShapeAppearanceModel().toBuilder()
                .setBottomLeftCorner(CornerFamily.ROUNDED, px(35).toFloat())
                .setBottomRightCorner(CornerFamily.ROUNDED, px(35).toFloat()).build()
        ).apply {
            fillColor = ColorStateList.valueOf(getColor(R.color.white))
        }
        elevation = px(4).toFloat()
    }

    private fun addViews() {
        addView(yearMonthText)
        addView(downArrow)
        addView(todayButton)
        addWeekLayoutAndWeekTexts()
    }

    private fun addWeekLayoutAndWeekTexts() = weekTextLayout.also { layout ->
        addView(layout)
        weekTexts.forEach(layout::addView)
    }

    private fun onCurDateChanged() {
        curDateLiveData.value = curDate
        rowCount = calculateRequiredRow(curDate)

        setYearMonthTextWithDate(curDate)
        selectPagerItemsWithDate(curDate)

        onDateChangeListener?.invoke(curDate)
        changeWeekTextsColor()
        notifyScrollToTop()
        invalidate()
    }

    private fun setYearMonthTextWithDate(date: LocalDate) {
        yearMonthText.text = "${date.year}.${date.monthValue.padZero()}"
    }

    private fun selectPagerItemsWithDate(date: LocalDate) {
        val nextMonthlyIndex = convertDateToMonthlyIndex(date)
        val nextWeeklyIndex = convertDateToWeeklyIndex(date)

        if (monthlyViewPager?.currentItem != nextMonthlyIndex) {
            monthlyViewPager?.setCurrentItem(
                nextMonthlyIndex, false
            )
        }

        if (weeklyViewPager?.currentItem != nextWeeklyIndex) {
            weeklyViewPager?.setCurrentItem(
                nextWeeklyIndex, false
            )
        }
    }


    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
    }
    private val greyCapsulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.sub_grey_5)
        setShadowLayer(12f, 0f, 0f, getColor(R.color.sub_grey_5))
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
        val widthWithoutPadding = width - paddingHorizontal * 2f
        val rawWidth = widthWithoutPadding / 7f
        val maxWidth = pxFloat(42)
        val capsuleWidth = rawWidth.coerceAtMost(maxWidth)
        val capsuleLeftPadding = if (rawWidth >= maxWidth) (rawWidth - maxWidth) / 2f else 0f
        val capsuleHeight = pxFloat(64)
        val capsuleLeft = paddingHorizontal + capsuleLeftPadding + today.dayOfWeekIndex * rawWidth
        val capsuleWidthRadius = capsuleWidth / 2f
        val capsuleTop = pxFloat(72)

        val greyCapsuleLeft =
            paddingHorizontal + capsuleLeftPadding + selectedDate.dayOfWeekIndex * rawWidth
        if (isSelectedInCurrentWeek) {
            canvas.drawRoundRect(
                greyCapsuleLeft,
                capsuleTop,
                greyCapsuleLeft + capsuleWidth,
                capsuleTop + capsuleHeight,
                capsuleWidthRadius,
                capsuleWidthRadius,
                greyCapsulePaint
            )
        }

        if (isTodayInCurrentWeek) {
            canvas.drawRoundRect(
                capsuleLeft,
                capsuleTop,
                capsuleLeft + capsuleWidth,
                capsuleTop + capsuleHeight,
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

                    animValue =
                        ((event.y - collapsedHeight) / (expandedHeight - collapsedHeight)).clamp(
                            0f,
                            1.2f
                        )
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
        height =
            MathUtils.lerp(collapsedHeight.toFloat(), expandedHeight.toFloat(), animValue).toInt()
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
        greyCapsulePaint.alpha = (255 - animValue * 255).toInt().clamp(0, 255)
    }

    private fun animPagersAlpha() {
        weeklyViewPager?.alpha = 1 - animValue
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
        weeklyViewPager?.isUserInputEnabled = true
        monthlyViewPager?.isUserInputEnabled = false

        if (weeklyViewPager != null && monthlyViewPager != null) {
            removeViewAt(0)
            removeViewAt(0)
            addView(monthlyViewPager, 0)
            addView(weeklyViewPager, 1)
        }
    }

    private fun enableTouchMonthlyPagerOnly() {
        weeklyViewPager?.isUserInputEnabled = false
        monthlyViewPager?.isUserInputEnabled = true

        if (weeklyViewPager != null && monthlyViewPager != null) {
            removeViewAt(0)
            removeViewAt(0)
            addView(weeklyViewPager, 0)
            addView(monthlyViewPager, 1)
        }
    }

    companion object {
        private const val parentId = ConstraintSet.PARENT_ID
        private const val MIN_HEIGHT_DP = 224
        private const val EXPAND_MARGIN_BOTTOM_DP = 120
    }
}