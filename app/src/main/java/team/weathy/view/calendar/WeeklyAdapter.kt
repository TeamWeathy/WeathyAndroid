package team.weathy.view.calendar

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import team.weathy.view.calendar.CalendarView.CalendarDate.Companion.convertWeeklyIndexToDate
import team.weathy.view.calendar.WeeklyAdapter.WeeklyHolder

class WeeklyAdapter(
    private val animLiveData: LiveData<Float>,
) : RecyclerView.Adapter<WeeklyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyHolder {
        return WeeklyHolder(WeeklyView(parent.context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            animLiveData = this@WeeklyAdapter.animLiveData
        })
    }

    override fun getItemCount(): Int {
        return MAX_ITEM_COUNT
    }

    override fun onBindViewHolder(holder: WeeklyHolder, position: Int) {
        holder.bind(position)
    }

    inner class WeeklyHolder(private val view: WeeklyView) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            view.date = convertWeeklyIndexToDate(position)
        }
    }

    companion object {
        const val MAX_ITEM_COUNT = Int.MAX_VALUE
    }
}
