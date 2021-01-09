package team.weathy.view.calendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.IntRange
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import team.weathy.R
import team.weathy.databinding.ViewCalendarItemBinding
import team.weathy.model.entity.CalendarPreview
import team.weathy.util.OnChangeProp
import team.weathy.util.calculateRequiredRow
import team.weathy.util.extensions.clamp
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.getMonthTexts
import team.weathy.util.margin
import java.time.LocalDate

class MonthlyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private val today = LocalDate.now()
    private var todayIndex = 0
    var date: LocalDate by OnChangeProp(LocalDate.now()) {
        updateUIWithDate()
    }
    var data: List<CalendarPreview?>? by OnChangeProp(null) {
        updateUIWithData()
    }

    private val isTodayInCurrentMonth
        get() = date.year == today.year && date.month == today.month

    var animValue: Float by OnChangeProp(0f) {
        adjustUIsWithAnimValue()
    }
    var scrollEnabled: Boolean by OnChangeProp(true) {
        if (it) enableScroll()
        else disableScroll()
    }

    private val scrollView = ScrollView(context).apply {
        id = ViewCompat.generateViewId()
        overScrollMode = OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false

        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
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

            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, px(ITEM_HEIGHT_6ROW_DP), 0f)
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

    private val topBlur = View(context).apply {
        setBackgroundResource(R.drawable.blur_white_top_down)

        layoutParams = LayoutParams(MATCH_PARENT, px(16)).apply {
            gravity = Gravity.TOP
        }
    }
    private val bottomBlur = View(context).apply {
        setBackgroundResource(R.drawable.blur_white_bottom_up)

        layoutParams = LayoutParams(MATCH_PARENT, px(16)).apply {
            gravity = Gravity.BOTTOM
        }
    }

    init {
        addView(scrollView)
        addOuterLinearLayout()
        addView(topBlur)
        addView(bottomBlur)
        updateUIWithDate()
    }


    private fun addOuterLinearLayout() {
        scrollView.addView(outerLinearLayout)
    }

    private fun updateUIWithDate() {
        val (texts, startDayIndex, endDayIndex) = getMonthTexts(date)
        todayIndex = if (isTodayInCurrentMonth) today.dayOfMonth + (7 - startDayIndex) + 2 else -1

        innerLinearLayouts.forEach {
            it.removeAllViews()
        }
        outerLinearLayout.removeAllViews()
        val requiredRow = calculateRequiredRow(date)
        val rowHeight = when (requiredRow) {
            4 -> px(ITEM_HEIGHT_4ROW_DP)
            5 -> px(ITEM_HEIGHT_5ROW_DP)
            else -> px(ITEM_HEIGHT_6ROW_DP)
        }
        val coldestViewMarginBottom = when (requiredRow) {
            4 -> px(20)
            5 -> px(8)
            else -> px(3)
        }

        repeat(requiredRow) { row ->
            val innerLinearLayout = innerLinearLayouts[row]

            if (row > 0) outerLinearLayout.addView(dividerGenerator())
            outerLinearLayout.addView(innerLinearLayout)
            innerLinearLayout.updateLayoutParams {
                height = rowHeight
            }

            repeat(7) { column ->
                val idx = row * 7 + column

                val item = calendarItems[idx]

                item.day.text = texts[idx].toString()

                val isInCurrentMonthRange = !(idx < startDayIndex || idx > endDayIndex)
                val isFuture = isTodayInCurrentMonth && texts[idx] > today.dayOfMonth
                val shouldBeDisabled = !isInCurrentMonthRange || isFuture

                item.root.alpha = if (shouldBeDisabled) .3f else 1f

                item.circleSmall.isVisible = idx == todayIndex
                item.day.setTextColor(getDayTextColor(idx % 7, idx == todayIndex))

                item.tempLow.margin.bottom = coldestViewMarginBottom

                innerLinearLayout.addView(
                    item.root, LinearLayout.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT, 1f
                    )
                )
            }
        }
    }

    private fun updateUIWithData() {
        calendarItems.forEachIndexed { index, binding ->
            data?.getOrNull(index)?.let {
                binding.tempHigh.isVisible = true
                binding.tempLow.isVisible = true

                binding.tempHigh.text = it.temperature.maxTemp.toString()
                binding.tempLow.text = it.temperature.minTemp.toString()
            } ?: run {
                binding.tempHigh.isVisible = false
                binding.tempLow.isVisible = false
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
        val itemsExceptFirstLine = calendarItems.subList(7, calendarItems.size)
        itemsExceptFirstLine.forEach { binding ->
            binding.root.translationY = animValue * 10f
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

    fun scrollToTop() {
        scrollView.smoothScrollTo(0, 0)
    }

    companion object {
        const val ITEM_HEIGHT_4ROW_DP = 117
        const val ITEM_HEIGHT_5ROW_DP = 94
        const val ITEM_HEIGHT_6ROW_DP = 81
    }
}