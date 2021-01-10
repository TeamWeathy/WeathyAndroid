package team.weathy.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import team.weathy.R
import team.weathy.databinding.ItemWeeklyWeatherBinding
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.px
import team.weathy.util.koFormat
import java.time.LocalDateTime
import kotlin.random.Random

class WeeklyWeatherView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val curTime = LocalDateTime.now()

    private val weeks = (0 until ITEM_COUNT).map { idx ->
        if (idx == 0) "오늘"
        else curTime.plusDays(idx.toLong()).dayOfWeek.koFormat
    }

    private val weatherItems = (0 until ITEM_COUNT).map { idx ->
        ItemWeeklyWeatherBinding.inflate(LayoutInflater.from(context), this, false).also {
            it.root.layoutParams = LayoutParams(0, px(125), 1f)

            it.week.setTextColor(getColor(if (idx == 0) R.color.main_mint else R.color.sub_grey_6))
            it.week.text = weeks[idx]
        }
    }

    private val animators: MutableList<Animator> = mutableListOf()

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
        val duration2 = 600L

        weatherItems.forEach { binding ->
            val lowDest = Random.nextInt(-20, 0)
            val highDest = Random.nextInt(0, 20)

            ValueAnimator.ofInt(0, lowDest).apply {
                addUpdateListener {
                    val value = it.animatedValue as Int
                    binding.tempLow.text = "${value}°"
                }
                duration = duration1
                start()

                animators.add(this)
            }

            ValueAnimator.ofInt(0, highDest).apply {
                addUpdateListener {
                    val value = it.animatedValue as Int
                    binding.tempHigh.text = "${value}°"
                }
                duration = duration2
                startDelay = duration1
                start()

                animators.add(this)
            }

            ValueAnimator.ofInt(1, px(20)).apply {
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
    }
}