package team.weathy.view.calendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
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
import team.weathy.util.Once
import team.weathy.util.dpFloat
import team.weathy.util.extensions.clamp
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import java.time.LocalDate

class MonthlyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ScrollView(context, attrs) {

    private val today = LocalDate.now()
    var curDate: LocalDate by OnChangeProp(LocalDate.now()) {
        updateUIWithDate()
    }

    private val isTodayInCurrentMonth
        get() = curDate.year == today.year && curDate.month == today.month

    lateinit var animLiveData: LiveData<Float>
    private val animValue
        get() = animLiveData.value!!
    lateinit var scrollEnabled: LiveData<Boolean>
    lateinit var onScrollToTop: LiveData<Once<Unit>>

    private val animValueObserver = Observer<Float> {
        adjustUIsWithAnimValue()
    }
    private val scrollEnabledObserver = Observer<Boolean> {
        if (it) enableScroll()
        else disableScroll()
    }
    private val onScrollToTopObserver = Observer<Once<Unit>> {
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
    private val dividerGenerator = {
        View(context).apply {
            id = ViewCompat.generateViewId()
            setBackgroundColor(getColor(R.color.sub_grey_5))
        }
    }

    init {
        configureContainer()
        addViews()
        updateUIWithDate()
    }

    private fun configureContainer() {
        id = ViewCompat.generateViewId()
        overScrollMode = OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
    }

    private fun addViews() {
        addCalendarItems()
    }

    private fun addCalendarItems() {
        addView(
            outerLinearLayout, ConstraintLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        innerLinearLayout.forEachIndexed { index, inner ->
            if (index != 0) outerLinearLayout.addView(
                dividerGenerator(),
                LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, px(1), 0f)
            )
            outerLinearLayout.addView(
                inner, LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, px(102), 0f)
            )

            calendarItems.subList(index * 7, index * 7 + 7).forEach {
                inner.addView(
                    it.root, LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        1f
                    )
                )
            }
        }
    }

    private fun updateUIWithDate() {
        calendarItems.forEachIndexed { idx, binding ->
            val isToday = isTodayInCurrentMonth && idx + 1 == today.dayOfMonth
            binding.circleSmall.isVisible = isToday
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

        if (isTodayInCurrentMonth) {
            val itemToday = calendarItems[today.dayOfMonth - 1]
            itemToday.run {
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