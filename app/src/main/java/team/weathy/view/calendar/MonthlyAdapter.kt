package team.weathy.view.calendar

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import team.weathy.util.Once
import team.weathy.util.convertMonthlyIndexToDate
import team.weathy.view.calendar.MonthlyAdapter.MonthlyHolder

class MonthlyAdapter(
    private val animLiveData: LiveData<Float>,
    private val scrollEnabled: LiveData<Boolean>,
    private val onScrollToTop: LiveData<Once<Unit>>
) : RecyclerView.Adapter<MonthlyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyHolder {
        return MonthlyHolder(MonthlyView(parent.context).apply {
            animLiveData = this@MonthlyAdapter.animLiveData
            scrollEnabled = this@MonthlyAdapter.scrollEnabled
            onScrollToTop = this@MonthlyAdapter.onScrollToTop
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
        fun bind(position: Int) {
            view.curDate = convertMonthlyIndexToDate(position)
        }
    }

    companion object {
        const val MAX_ITEM_COUNT = Int.MAX_VALUE
    }
}
