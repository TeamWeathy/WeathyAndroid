package team.weathy.ui.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import team.weathy.databinding.ItemLocationBinding
import team.weathy.model.entity.Location
import team.weathy.ui.main.search.SearchAdapter.Holder
import team.weathy.util.AnimUtil
import team.weathy.util.SwipeMenuTouchListener
import team.weathy.util.debugE

class SearchAdapter : Adapter<Holder>() {
    var items: List<Location> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationBinding.inflate(inflater, parent, false)

        return Holder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])

    fun submitList(list: List<Location>) {
        items = list
        notifyDataSetChanged()
    }

    inner class Holder(private val binding: ItemLocationBinding) : ViewHolder(binding.root),
        SwipeMenuTouchListener.Callback {
        private val swipeMenuTouchListener = SwipeMenuTouchListener(this)

        init {
            binding.contentContainer.setOnTouchListener(swipeMenuTouchListener)
        }

        override fun onTranslateXChanged(x: Float) {
            binding.contentContainer.translationX = x
        }

        override fun onMenuOpened() {
            AnimUtil.runSpringAnimation(binding.contentContainer.translationX, -binding.root.width.toFloat(), 1f) {
                binding.contentContainer.translationX = it
            }
        }

        override fun onMenuClosed() {
            AnimUtil.runSpringAnimation(binding.contentContainer.translationX, 0f, 1f) {
                binding.contentContainer.translationX = it
            }
        }

        override fun onDeleted() {
        }

        fun bind(item: Location) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}

@BindingAdapter("app:recyclerview_")
fun RecyclerView.setItems(items: List<Location>?) {
    if (items == null) return
    (adapter as? SearchAdapter)?.run {
        submitList(items)
    }
}
