package team.weathy.view.calendar

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import team.weathy.util.Once
import team.weathy.view.calendar.MonthlyAdapter.MonthlyHolder

class MonthlyAdapter(
    private val animLiveData: LiveData<Float>,
    private val scrollEnabled: LiveData<Boolean>,
    private val onScrollToTop: LiveData<Once<Unit>>
) : ListAdapter<Any, MonthlyHolder>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyHolder {
        return MonthlyHolder(MonthlyView(parent.context).apply {
            animLiveData = this@MonthlyAdapter.animLiveData
            scrollEnabled = this@MonthlyAdapter.scrollEnabled
            onScrollToTop = this@MonthlyAdapter.onScrollToTop
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
    }

    override fun onBindViewHolder(holder: MonthlyHolder, position: Int) = holder.bind(getItem(position))

    inner class MonthlyHolder(private val root: MonthlyView) : RecyclerView.ViewHolder(root) {
        fun bind(item: Any) {
            //            binding.item = item
            //            binding.executePendingBindings()
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return false
            }
        }
    }
}
