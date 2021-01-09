package team.weathy.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import team.weathy.R
import team.weathy.databinding.ItemHourlyWeatherBinding

class WeeklyWeatherView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val weatherItems = (0..6).map {
        ItemHourlyWeatherBinding.bind(LayoutInflater.from(context).inflate(R.layout.item_hourly_weather, this, false))
            .also {
                it.root.layoutParams = LayoutParams(0, WRAP_CONTENT, 1f)
            }
    }

    init {
        initContainer()
        addViews()
    }

    private fun initContainer() {
        orientation = HORIZONTAL
        weightSum = 7f
    }

    private fun addViews() {
        weatherItems.forEach {
            addView(it.root)
        }
    }
}