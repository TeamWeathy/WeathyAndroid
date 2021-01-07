package team.weathy.view.calendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.math.MathUtils
import team.weathy.R
import team.weathy.databinding.ViewCalendarItemBinding
import team.weathy.util.OnChangeProp
import team.weathy.util.SimpleEventLiveData
import team.weathy.util.calculateRequiredRow
import team.weathy.util.dpFloat
import team.weathy.util.extensions.clamp
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.getMonthTexts
import java.time.LocalDate

class MonthlyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ScrollView(context, attrs) {

    private val today = LocalDate.now()
    private var todayIndex = 0
    var date: LocalDate by OnChangeProp(LocalDate.now()) {
        updateUIWithDate()
    }

    private val isTodayInCurrentMonth
        get() = date.year == today.year && date.month == today.month

    lateinit var animLiveData: LiveData<Float>
    private val animValue
        get() = animLiveData.value!!
    lateinit var scrollEnabled: LiveData<Boolean>
    lateinit var onScrollToTop: SimpleEventLiveData

    private val animValueObserver = Observer<Float> {
        adjustUIsWithAnimValue()
    }
    private val scrollEnabledObserver = Observer<Boolean> {
        if (it) enableScroll()
        else disableScroll()
    }
    private val onScrollToTopObserver = Observer<Unit> {
        scrollToTop()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animLiveData.observeForever(animValueObserver)
        scrollEnabled.observeForever(scrollEnabledObserver)
        onScrollToTop.observeForever(onScrollToTopObserver)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        animLiveData.removeObserver(animValueObserver)
        scrollEnabled.removeObserver(scrollEnabledObserver)
        onScrollToTop.removeObserver(onScrollToTopObserver)
    }


    private val outerLinearLayout = LinearLayout(context).apply {
        id = ViewCompat.generateViewId()
        orientation = LinearLayout.VERTICAL
        weightSum = 5f

        layoutParams = LayoutParams(
            MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    private val innerLinearLayouts = (1..6).map {
        LinearLayout(context).apply {
            id = ViewCompat.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            weightSum = 7f

            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, px(102), 0f)
        }
    }

    private val calendarItems = (1..42).map {
        ViewCalendarItemBinding.inflate(LayoutInflater.from(context), null, false).apply {
            root.id = ViewCompat.generateViewId()
        }
    }
    private val dividerGenerator = {
        View(context).apply {
            id = ViewCompat.generateViewId()
            setBackgroundColor(getColor(R.color.sub_grey_5))

            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, px(1), 0f)
        }
    }

    init {
        configureContainer()
        addOuterLinearLayout()
        updateUIWithDate()
    }

    private fun configureContainer() {
        id = ViewCompat.generateViewId()
        overScrollMode = OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
    }

    private fun addOuterLinearLayout() {
        addView(outerLinearLayout)
    }

    private fun updateUIWithDate() {
        val (texts, startDayIndex, endDayIndex) = getMonthTexts(date)
        todayIndex = if (isTodayInCurrentMonth) today.dayOfMonth + (7 - startDayIndex) + 2 else -1

        innerLinearLayouts.forEach {
            it.removeAllViews()
        }
        outerLinearLayout.removeAllViews()
        val requiredRow = calculateRequiredRow(date)

        repeat(requiredRow) { row ->
            val innerLinearLayout = innerLinearLayouts[row]

            if (row > 0) outerLinearLayout.addView(dividerGenerator())
            outerLinearLayout.addView(innerLinearLayout)

            repeat(7) { column ->
                val idx = row * 7 + column

                val item = calendarItems[idx]

                item.day.text = texts[idx].toString()

                val isInCurrentMonthRange = !(idx < startDayIndex || idx > endDayIndex)
                val isFuture = isTodayInCurrentMonth && texts[idx] > today.dayOfMonth
                val shouldBeDisabled = !isInCurrentMonthRange || isFuture

                item.root.alpha = if (shouldBeDisabled) .3f else 1f
                item.tempHigh.isVisible = !shouldBeDisabled
                item.tempLow.isVisible = !shouldBeDisabled

                item.circleSmall.isVisible = idx == todayIndex
                item.day.setTextColor(getDayTextColor(idx % 7, idx == todayIndex))

                innerLinearLayout.addView(
                    item.root, LinearLayout.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT, 1f
                    )
                )
            }
        }
    }


    private fun getDayTextColor(@IntRange(from = 0L, to = 6L) week: Int, isToday: Boolean = false): Int {
        if (isToday) return Color.WHITE

        return getColorFromWeek(week)
    }

    private fun getColorFromWeek(@IntRange(from = 0L, to = 6L) week: Int) = getColor(
        when (week % 7) {
            6 -> R.color.blue_temp
            0 -> R.color.red_temp
            else -> R.color.main_grey
        }
    )

    private fun adjustUIsWithAnimValue() {
        adjustCalendarItems()
    }

    private fun adjustCalendarItems() {
        calendarItems.forEach { binding ->
            binding.day.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = MathUtils.lerp(5.dpFloat, 15.dpFloat, animValue).toInt()
            }
        }

        val itemsExceptFirstLine = calendarItems.subList(7, calendarItems.size)
        itemsExceptFirstLine.forEachIndexed { index, binding ->
            binding.root.translationY = MathUtils.lerp(index * 4f, 0f, animValue)
        }

        if (todayIndex != -1) {
            calendarItems[todayIndex].run {
                circleSmall.scaleX = (animValue + 0.3f).clamp(0.5f, 1.0f)
                circleSmall.scaleY = (animValue + 0.3f).clamp(0.5f, 1.0f)
            }
        }
    }

    private fun disableScroll() {
        setOnTouchListener { _, _ -> true }
    }

    private fun enableScroll() {
        setOnTouchListener(null)
    }

    private fun scrollToTop() {
        smoothScrollTo(0, 0)
    }
}