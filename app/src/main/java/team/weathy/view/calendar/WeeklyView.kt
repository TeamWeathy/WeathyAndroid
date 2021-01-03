package team.weathy.view.calendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import team.weathy.R
import team.weathy.databinding.ViewCalendarWeeklyItemBinding
import team.weathy.util.DateTime
import team.weathy.util.extensions.getColor

class WeeklyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    var week = DateTime.now()
    private val today = DateTime.now()

    private val isTodayInCurrentWeek
        get() = week.year == today.year && week.month == today.month && week.weekOfMonth() == today.weekOfMonth()

    private val calendarItems = (0..6).map {
        ViewCalendarWeeklyItemBinding.inflate(LayoutInflater.from(context), null, false).apply {
            root.id = ViewCompat.generateViewId()

            day.text = (it + 1).toString()
        }
    }

    lateinit var animLiveData: LiveData<Float>
    private val animValue
        get() = animLiveData.value!!
    private val animValueObserver = Observer<Float> { animValue ->
        calendarItems.forEach {
            it.circle.scaleX = 1 - animValue
            it.circle.scaleY = 1 - animValue
        }
    }

    init {
        configureContainer()
        addCalendarItems()
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
            val isToday = isTodayInCurrentWeek && today.day == idx + 1
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