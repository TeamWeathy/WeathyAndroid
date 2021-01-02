package team.weathy.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.ViewCompat
import team.weathy.databinding.ViewCalendarItemBinding

class MonthlyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ScrollView(context, attrs) {

//    private val outerLinearLayout = LinearLayout(context).apply {
//        id = ViewCompat.generateViewId()
//        orientation = LinearLayout.VERTICAL
//        weightSum = 5f
//        setBackgroundColor(Color.TRANSPARENT)
//    }
//    private val innerLinearLayout = (1..5).map {
//        LinearLayout(context).apply {
//            id = ViewCompat.generateViewId()
//            orientation = LinearLayout.HORIZONTAL
//            weightSum = 7f
//            setBackgroundColor(Color.TRANSPARENT)
//        }
//    }
//    private val calendarItems = (0..34).map {
//        ViewCalendarItemBinding.inflate(LayoutInflater.from(context), null, false).apply {
//            root.id = ViewCompat.generateViewId()
//
//            day.text = (it + 1).toString()
//        }
//    }

    init {
        configureContainer()
    }

    private fun configureContainer() {
        id = ViewCompat.generateViewId()
        overScrollMode = OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
    }
}