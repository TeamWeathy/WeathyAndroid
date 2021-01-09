package team.weathy.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewPropertyAnimator
import android.widget.LinearLayout
import androidx.core.animation.doOnStart
import team.weathy.R
import team.weathy.databinding.ItemHourlyWeatherBinding
import team.weathy.util.extensions.pxFloat
import team.weathy.util.padZero
import java.time.LocalDateTime
import kotlin.random.Random

class WeeklyWeatherView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val curTime = LocalDateTime.now()
    private val timeTexts = (0 until ITEM_COUNT).map { "${curTime.hour + it}시".padZero(3) }

    private val weatherItems = (0 until ITEM_COUNT).map { idx ->
        ItemHourlyWeatherBinding.bind(LayoutInflater.from(context).inflate(R.layout.item_hourly_weather, this, false))
            .also {
                it.hour.text = timeTexts[idx]

                it.root.layoutParams = LayoutParams(0, WRAP_CONTENT, 1f)
            }
    }

    private val textAnimators: MutableList<Animator> = mutableListOf()
    private val viewAnimators: MutableList<ViewPropertyAnimator> = mutableListOf()

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



    fun resetAnimation() {
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
        resetAnimation()
        weatherItems.forEach { binding ->
            binding.pop.animate().alpha(1f).translationY(0f).setDuration(1200L).setStartDelay(500L).apply {
                viewAnimators.add(this)
                start()
            }
            binding.temp.animate().alpha(1f).translationY(0f).setDuration(700L).apply {
                viewAnimators.add(this)
            }.start()

            val dest = Random.nextInt(-20, 20)
            ValueAnimator.ofInt(0, dest).apply {
                doOnStart {
                    binding.temp.text = "0°"
                }
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
        private const val ITEM_COUNT = 7
    }
}