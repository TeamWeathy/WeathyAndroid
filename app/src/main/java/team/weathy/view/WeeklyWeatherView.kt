package team.weathy.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import team.weathy.R
import team.weathy.databinding.ItemWeeklyWeatherBinding
import team.weathy.model.entity.DailyWeatherWithInDays
import team.weathy.model.entity.Weather
import team.weathy.util.OnChangeProp
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.getFont
import team.weathy.util.extensions.px
import team.weathy.util.koFormat
import java.time.LocalDateTime

class WeeklyWeatherView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val curTime = LocalDateTime.now()

    private val weeks = (0 until ITEM_COUNT).map { idx ->
        if (idx == 0) "오늘"
        else curTime.plusDays(idx.toLong()).dayOfWeek.koFormat
    }

    private val weatherItems = (0 until ITEM_COUNT).map { idx ->
        ItemWeeklyWeatherBinding.inflate(LayoutInflater.from(context), this, false).also {
            it.root.layoutParams = LayoutParams(0, px(129), 1f)

            it.week.typeface = context.getFont(R.font.notosans_medium)
            it.week.setTextColor(getColor(if (idx == 0) R.color.mint_icon else R.color.sub_grey_6))
            it.week.text = weeks[idx]
        }
    }

    private val animators: MutableList<Animator> = mutableListOf()

    var weathers: List<DailyWeatherWithInDays> by OnChangeProp(listOf()) {
        it.forEachIndexed { index, weather ->
            weatherItems.getOrNull(index)?.run {
                icon.setImageResource(weather.weather.smallIconId)
                tempHigh.text = "${weather.temperature.maxTemp}°"
                tempLow.text = "${weather.temperature.minTemp}°"
            }
        }
    }

    init {
        initContainer()
        addViews()
        resetAnimation()
    }

    private fun initContainer() {
        clipChildren = false
        orientation = HORIZONTAL
        weightSum = 7f
    }

    private fun addViews() {
        weatherItems.forEach {
            addView(it.root)
        }
    }


    private var isAnimDone = false
    fun resetAnimation() {
        isAnimDone = false
        animators.forEach { it.cancel() }
        animators.clear()

        weatherItems.forEach { binding ->
            binding.tempBetweenBar.updateLayoutParams {
                height = 1
            }
            binding.tempHigh.text = "0°"
            binding.tempLow.text = "0°"
            binding.tempHigh.alpha = 0f
            binding.tempLow.alpha = 0f
            binding.tempBetweenBar.alpha = 0f
        }
    }


    fun startAnimation() {
        if (isAnimDone) return
        isAnimDone = true

        val duration1 = 600L
        val duration2 = 1100L

        weatherItems.forEachIndexed { idx, binding ->
            val weather = weathers.getOrNull(idx) ?: return@forEachIndexed

            ValueAnimator.ofInt(0, weather.temperature.minTemp).apply {
                addUpdateListener {
                    val value = it.animatedValue as Int
                    binding.tempLow.text = "${value}°"
                }
                duration = duration1
                start()

                animators.add(this)
            }

            ValueAnimator.ofInt(0, weather.temperature.maxTemp).apply {
                addUpdateListener {
                    val value = it.animatedValue as Int
                    binding.tempHigh.text = "${value}°"
                }
                duration = duration2
                startDelay = duration1
                start()

                animators.add(this)
            }

            ValueAnimator.ofInt(1, px(16)).apply {
                addUpdateListener {
                    val value = it.animatedValue as Int
                    binding.tempBetweenBar.updateLayoutParams {
                        height = value
                    }
                }
                duration = duration2
                startDelay = duration1
                start()

                animators.add(this)
            }

            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.tempLow.alpha = value
                }
                duration = duration1
                start()

                animators.add(this)
            }

            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.tempHigh.alpha = value
                    binding.tempBetweenBar.alpha = value
                }
                duration = 600L
                startDelay = 600L
                start()

                animators.add(this)
            }
        }
    }

    companion object {
        private const val ITEM_COUNT = 7

        @JvmStatic
        @BindingAdapter("weeklyWeathers")
        fun WeeklyWeatherView.setWeeklyWeathers(weathers: List<DailyWeatherWithInDays>) {
            this.weathers = weathers
        }
    }
}
