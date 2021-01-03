package team.weathy.view.calendar

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import team.weathy.view.calendar.WeeklyAdapter.WeeklyHolder

class WeeklyAdapter() : ListAdapter<Any, WeeklyHolder>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyHolder {
        return WeeklyHolder(WeeklyView(parent.context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
    }

    override fun onBindViewHolder(holder: WeeklyHolder, position: Int) = holder.bind(getItem(position))

    inner class WeeklyHolder(private val root: WeeklyView) : RecyclerView.ViewHolder(root) {
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
