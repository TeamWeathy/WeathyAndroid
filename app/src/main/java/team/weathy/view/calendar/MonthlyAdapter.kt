package team.weathy.view.calendar

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import team.weathy.model.entity.CalendarPreview
import team.weathy.ui.main.calendar.YearMonthFormat
import team.weathy.util.SimpleEventLiveData
import team.weathy.util.convertMonthlyIndexToDateToFirstDateOfMonthCalendar
import team.weathy.util.yearMonthFormat
import team.weathy.view.calendar.MonthlyAdapter.MonthlyHolder
import java.time.LocalDate

class MonthlyAdapter(
    private val animLiveData: LiveData<Float>,
    private val scrollEnabled: LiveData<Boolean>,
    private val onScrollToTop: SimpleEventLiveData,
    private val data: LiveData<Map<YearMonthFormat, List<CalendarPreview?>>>,
    private val selectedDate: LiveData<LocalDate>,
    private val lifecycleOwner: LifecycleOwner,
    private val onClickDateListener: (date: LocalDate) -> Unit,
) : RecyclerView.Adapter<MonthlyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyHolder {
        return MonthlyHolder(MonthlyView(parent.context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
    }

    override fun getItemCount(): Int {
        return MAX_ITEM_COUNT
    }

    override fun onBindViewHolder(holder: MonthlyHolder, position: Int) {
        holder.bind(position)
    }

    inner class MonthlyHolder(private val view: MonthlyView) : RecyclerView.ViewHolder(view) {
        init {
            animLiveData.observe(lifecycleOwner) {
                view.animValue = it
            }
            scrollEnabled.observe(lifecycleOwner) {
                view.scrollEnabled = it
            }
            onScrollToTop.observe(lifecycleOwner) {
                view.scrollToTop()
            }
            data.observe(lifecycleOwner) {
                view.data = it[view.firstDateInMonth.yearMonthFormat]
            }
            selectedDate.observe(lifecycleOwner) {
                view.selectedDate = it
            }
            view.onClickDateListener = onClickDateListener
        }

        fun bind(position: Int) {
            val (firstDateOfMonthInCalendar, firstDateOfMonth) = convertMonthlyIndexToDateToFirstDateOfMonthCalendar(
                position
            )

            view.firstDatesInCalednarAndMonth = firstDateOfMonthInCalendar to firstDateOfMonth

            view.data = data.value?.get(firstDateOfMonth.yearMonthFormat)
        }
    }

    companion object {
        const val MAX_ITEM_COUNT = Int.MAX_VALUE
    }
}
