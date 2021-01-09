package team.weathy.ui.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import team.weathy.databinding.ItemLocationBinding
import team.weathy.model.entity.OverviewWeather
import team.weathy.ui.main.search.SearchAdapter.Holder
import team.weathy.util.AnimUtil
import team.weathy.util.LocationItemMenuSwiper
import team.weathy.util.LocationItemMenuSwiper.Callback
import team.weathy.util.dateHourString
import team.weathy.util.koFormat
import team.weathy.util.setOnDebounceClickListener
import java.time.LocalDateTime

class SearchAdapter(private val onItemRemoved: (idx: Int) -> Unit) : Adapter<Holder>() {
    private val items = mutableListOf<OverviewWeather>()
    private val menuOpens = mutableListOf<Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationBinding.inflate(inflater, parent, false)

        return Holder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])

    fun submitList(list: List<OverviewWeather>) {
        items.clear()
        menuOpens.clear()
        items.addAll(list)
        menuOpens.addAll(List(items.size) { false })
        notifyDataSetChanged()
    }

    inner class Holder(private val binding: ItemLocationBinding) : ViewHolder(binding.root) {
        private var isFirstBinding = true
        private var itemHeight = 0

        init {
            binding.deleteContainer setOnDebounceClickListener {
                swiper.deleteMenu()
            }
            // get item height dynamically
            binding.root.doOnLayout {
                itemHeight = it.height
            }
        }

        private val swiper = LocationItemMenuSwiper.configure(binding, object : Callback {
            override fun onOpened() {
                menuOpens[layoutPosition] = true
            }

            override fun onClosed() {
                menuOpens[layoutPosition] = false
            }

            override fun onDeleted() {
                runDeleteAnimation {
                    removeItemFromCollection(layoutPosition)
                }
            }
        })


        fun bind(item: OverviewWeather) {
            if (isFirstBinding) {
                isFirstBinding = false
            } else {
                binding.root.alpha = 1f
                binding.root.updateLayoutParams {
                    if (itemHeight != 0) height = itemHeight
                }

                if (menuOpens[layoutPosition]) {
                    swiper.openMenu(false)
                } else {
                    swiper.closeMenu(false)
                }
            }

            binding.datetimeText = LocalDateTime.now().koFormat
            binding.locationText = item.daily.region.name
            binding.curTemp = item.hourly.temperature?.toString()?.plus("°") ?: ""
            binding.highTemp = "${item.daily.temperature.maxTemp}°"
            binding.lowTemp = "${item.daily.temperature.minTemp}°"

            binding.executePendingBindings()
        }

        fun runDeleteAnimation(onEnd: () -> Unit) {
            AnimUtil.runSpringAnimation(100f, 0f, stiffness = 1000f, dampingRatio = 1f, onEnd = onEnd) { raw ->
                val animValue = raw / 100f

                binding.root.alpha = (animValue).coerceIn(0f, 1f)
                binding.root.updateLayoutParams {
                    height = (itemHeight * animValue).toInt().coerceIn(0, itemHeight)
                }
            }
        }
    }

    fun removeItemFromCollection(position: Int) {
        items.removeAt(position)
        menuOpens.removeAt(position)
        notifyItemRemoved(position)
        onItemRemoved(position)
    }
}

@BindingAdapter("search_adapter_items")
fun RecyclerView.setItems(items: List<OverviewWeather>) {
    (adapter as? SearchAdapter)?.run {
        submitList(items)
    }
}
