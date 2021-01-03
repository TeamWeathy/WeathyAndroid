package team.weathy.view.calendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.core.view.ViewCompat
import team.weathy.R
import team.weathy.databinding.ViewCalendarWeeklyItemBinding
import team.weathy.util.OnChangeProp
import team.weathy.util.extensions.getColor

class WeeklyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    var today by OnChangeProp(1) {
        lineIndexIncludesToday = (it - 1) / 7
        updateUIWithToday()
    }
    private var lineIndexIncludesToday = 0

    private val calendarItems = (0..6).map {
        ViewCalendarWeeklyItemBinding.inflate(LayoutInflater.from(context), null, false).apply {
            root.id = ViewCompat.generateViewId()

            day.text = (it + 1).toString()
        }
    }

    init {
        configureContainer()
        addCalendarItems()
        updateUIWithToday()
    }

    private fun configureContainer() {
        id = ViewCompat.generateViewId()
        orientation = HORIZONTAL
        weightSum = 7f
        setBackgroundColor(Color.TRANSPARENT)
    }

    private fun addCalendarItems() {
        calendarItems.forEach {
            addView(
                it.root, LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    1f
                )
            )
        }
    }

    private fun updateUIWithToday() {
        calendarItems.forEachIndexed { idx, binding ->
            val isToday = idx + 1 == today
            binding.day.setTextColor(getDayTextColor(idx % 7, isToday))
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
}