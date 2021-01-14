package team.weathy.view.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import team.weathy.databinding.ViewCalendarWeeklyItemBinding
import team.weathy.model.entity.CalendarPreview
import team.weathy.util.OnChangeProp
import team.weathy.util.dayOfWeekIndex
import team.weathy.util.debugE
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.isAvailable
import team.weathy.util.setOnDebounceClickListener
import team.weathy.util.weekOfMonth
import java.time.LocalDate

class WeeklyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    var firstDateInCalendar: LocalDate by OnChangeProp(LocalDate.now()) {
        debugE("firstDateInCalendar $it")
        updateUIWithDate()
    }
    var data: List<CalendarPreview?>? by OnChangeProp(null) {
        updateUIWithData()
    }

    private val today = LocalDate.now()

    var onClickDateListener: ((date: LocalDate) -> Unit)? = null

    private val calendarItems = (0..6).map {
        ViewCalendarWeeklyItemBinding.inflate(LayoutInflater.from(context), null, false).apply {
            root.id = ViewCompat.generateViewId()
        }
    }

    var animValue: Float by OnChangeProp(0f) {
        calendarItems.forEach { binding ->
            binding.circle.scaleX = 1 - animValue
            binding.circle.scaleY = 1 - animValue
            binding.circle.translationY = animValue * 10f
        }
    }

    init {
        configureContainer()
        addCalendarItems()
        updateUIWithDate()
        setOnDateClickListener()
    }

    private fun configureContainer() {
        id = ViewCompat.generateViewId()
        orientation = HORIZONTAL
        weightSum = 7f
        setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setOnDateClickListener() {
        calendarItems.forEachIndexed { idx, binding ->
            binding.root setOnDebounceClickListener {
                val date = firstDateInCalendar.plusDays(idx.toLong())
                onClickDateListener?.invoke(date)
            }
        }
    }

    private fun addCalendarItems() {
        calendarItems.forEach {
            addView(
                it.root, LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, px(ITEM_HEIGHT_DP), 1f
                )
            )
        }
    }

    private fun updateUIWithDate() {
        val isTodayInCurrentWeek =
            firstDateInCalendar.year == today.year && firstDateInCalendar.month == today.month && firstDateInCalendar.weekOfMonth == today.weekOfMonth

        val dates = (0..6).map { firstDateInCalendar.plusDays(it.toLong()) }

        calendarItems.forEachIndexed { idx, binding ->
            binding.root.alpha = if (!dates[idx].isAvailable()) .3f else 1f

            val isToday = isTodayInCurrentWeek && today.dayOfWeekIndex == idx
            binding.day.setTextColor(getDayTextColor(idx % 7, isToday))

            binding.day.text = dates[idx].dayOfMonth.toString()
        }
    }

    private fun updateUIWithData() {
        calendarItems.forEachIndexed { index, binding ->
            data?.getOrNull(index)?.let {
                binding.circle.isVisible = true
                binding.circle.backgroundTintList = ColorStateList.valueOf(getColor(it.stampId.colorRes))
            } ?: run {
                binding.circle.isVisible = false
            }
        }
    }


    private fun getDayTextColor(@IntRange(from = 0L, to = 6L) week: Int, isToday: Boolean = false): Int {
        if (isToday) return Color.WHITE

        return getColor(CalendarUtil.getWeekBaseColor(week))
    }

    companion object {
        const val ITEM_HEIGHT_DP = 81
    }
}