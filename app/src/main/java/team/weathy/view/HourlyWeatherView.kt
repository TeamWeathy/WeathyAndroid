package team.weathy.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import team.weathy.R
import team.weathy.databinding.ItemHourlyWeatherBinding
import team.weathy.model.entity.HourlyWeather
import team.weathy.util.OnChangeProp
import team.weathy.util.dp
import team.weathy.util.extensions.pxFloat
import team.weathy.util.padZero
import java.time.LocalDateTime

class HourlyWeatherView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private val curTime = LocalDateTime.now()

    private val scrollView = HorizontalScrollView(context).apply {
        setPadding(12.dp, 0, 12.dp, 0)
        clipToPadding = false
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        clipChildren = false
        isHorizontalScrollBarEnabled = false
    }
    private val linearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    private val initialTimeTexts = (0 until ITEM_COUNT).map { "${(curTime.hour + it) % 24}시".padZero(3) }
    private val weatherItems = (0 until ITEM_COUNT).map { index ->
        ItemHourlyWeatherBinding.inflate(LayoutInflater.from(context), this, false).also {
            it.root.layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT)

            val isToday = index == 0
            it.hour.isInvisible = isToday
            it.todayCircle.isInvisible = !isToday

            it.hour.text = initialTimeTexts[index]
        }
    }

    private val leftBlur = View(context).apply {
        setBackgroundResource(R.drawable.blur_white_left_right)
    }
    private val rightBlur = View(context).apply {
        setBackgroundResource(R.drawable.blur_white_right_left)
    }

    private val textAnimators: MutableList<Animator> = mutableListOf()
    private val viewAnimators: MutableList<ViewPropertyAnimator> = mutableListOf()

    var weathers: List<HourlyWeather> by OnChangeProp(listOf()) {
        val timeTexts = (0 until ITEM_COUNT).map { "${(curTime.hour + it) % 24}시".padZero(3) }
        it.forEachIndexed { index, weather ->
            weatherItems.getOrNull(index)?.run {
                icon.setImageResource(weather.climate.weather.iconId)
                pop.text = "${weather.pop}%°"
                temp.text = "${weather.temperature}°"

                hour.text = timeTexts[index]
            }
        }

        resetAnimation()
        startAnimation()
    }

    var position: Int by OnChangeProp(0) { pos ->
        weatherItems.forEachIndexed { index, binding ->
            val isToday = pos == 0 && index == 0

        }
    }

    init {
        addViews()
        resetAnimation()
        adjustViewWidths()
    }

    private fun addViews() {
        addView(scrollView)
        scrollView.addView(linearLayout)
        weatherItems.forEach {
            linearLayout.addView(it.root)
        }
        addView(leftBlur)
        addView(rightBlur)
    }

    private fun adjustViewWidths() {
        doOnLayout {
            val paddingHorizon = 24.dp
            val containerWidth = it.width
            val itemWidth = (containerWidth - paddingHorizon * 2) / 7f
            linearLayout.children.forEach {
                it.updateLayoutParams {
                    this.width = itemWidth.toInt()
                }
            }

            scrollView.doOnLayout {
                leftBlur.layoutParams = LayoutParams(12.dp, it.height).apply {
                    gravity = Gravity.LEFT
                }
                rightBlur.layoutParams = LayoutParams(12.dp, it.height).apply {
                    gravity = Gravity.RIGHT
                }
            }
        }
    }


    private var isAnimDone = false
    fun resetAnimation() {
        isAnimDone = false
        textAnimators.forEach { it.cancel() }
        viewAnimators.forEach { it.cancel() }
        textAnimators.clear()
        viewAnimators.clear()

        weatherItems.forEach { binding ->
            binding.pop.alpha = 0f
            binding.pop.translationY = -pxFloat(10)
            binding.temp.alpha = 0f
            binding.temp.translationY = pxFloat(10)
            binding.temp.text = "0°"
        }
    }


    fun startAnimation() {
        if (isAnimDone) return
        isAnimDone = true
        weatherItems.forEachIndexed { idx, binding ->
            val temperature = weathers.getOrNull(idx)?.temperature ?: return@forEachIndexed

            binding.pop.animate().alpha(1f).translationY(0f).setDuration(1200L).setStartDelay(500L).apply {
                withEndAction { isAnimDone = true }
                viewAnimators.add(this)
                start()
            }
            binding.temp.animate().alpha(1f).translationY(0f).setDuration(700L).apply {
                viewAnimators.add(this)
            }.start()

            ValueAnimator.ofInt(0, temperature).apply {
                addUpdateListener {
                    val value = it.animatedValue as Int
                    binding.temp.text = "${value}°"
                }
                duration = 1200L
                start()

                textAnimators.add(this)
            }
        }
    }

    companion object {
        private const val ITEM_COUNT = 24

        @JvmStatic
        @BindingAdapter("hourlyWeathers")
        fun HourlyWeatherView.setHourlyWeathers(weathers: List<HourlyWeather>) {
            this.weathers = weathers
        }
    }
}