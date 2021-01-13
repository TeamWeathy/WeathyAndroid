package team.weathy.view.calendar

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import team.weathy.model.entity.CalendarPreview
import team.weathy.ui.main.calendar.YearMonthFormat
import team.weathy.util.convertWeeklyIndexToFirstDateOfWeekCalendar
import team.weathy.util.debugE
import team.weathy.util.weekOfMonth
import team.weathy.util.yearMonthFormat
import team.weathy.view.calendar.WeeklyAdapter.WeeklyHolder
import java.time.LocalDate

class WeeklyAdapter(
    private val animLiveData: LiveData<Float>,
    private val data: LiveData<Map<YearMonthFormat, List<CalendarPreview?>>>,
    private val lifecycleOwner: LifecycleOwner,
    private val onClickDateListener: (date: LocalDate) -> Unit,
) : RecyclerView.Adapter<WeeklyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyHolder {
        return WeeklyHolder(WeeklyView(parent.context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
    }

    override fun getItemCount(): Int {
        return MAX_ITEM_COUNT
    }

    override fun onBindViewHolder(holder: WeeklyHolder, position: Int) {
        holder.bind(position)
    }

    inner class WeeklyHolder(private val view: WeeklyView) : RecyclerView.ViewHolder(view) {
        init {
            animLiveData.observe(lifecycleOwner) {
                view.animValue = it
            }
            data.observe(lifecycleOwner) {
                val weekOfMonth = view.firstDateInCalendar.weekOfMonth - 1
                view.data =
                    it[view.firstDateInCalendar.yearMonthFormat]?.subList(weekOfMonth * 7, (weekOfMonth + 1) * 7)
            }
            view.onClickDateListener = onClickDateListener
        }

        fun bind(position: Int) {
            val date = convertWeeklyIndexToFirstDateOfWeekCalendar(position)
            val weekOfMonth = date.weekOfMonth - 1
            view.firstDateInCalendar = date
            view.data = data.value?.get(date.yearMonthFormat)?.subList(weekOfMonth * 7, (weekOfMonth + 1) * 7)
        }
    }

    companion object {
        const val MAX_ITEM_COUNT = Int.MAX_VALUE
    }
}
