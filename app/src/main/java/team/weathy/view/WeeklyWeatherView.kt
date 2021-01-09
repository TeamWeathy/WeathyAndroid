package team.weathy.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class WeeklyWeatherView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {



    init {

    }

    private fun initContainer() {
        orientation = LinearLayout.HORIZONTAL
        weightSum = 7f
    }
}