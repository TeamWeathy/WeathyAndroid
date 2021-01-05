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
import team.weathy.util.LocationItemMenuSwiper
import team.weathy.util.LocationItemMenuSwiper.Callback
import team.weathy.util.setOnDebounceClickListener

class SearchAdapter : Adapter<Holder>() {
    private val items = mutableListOf<Location>()
    private val menuOpens = mutableListOf<Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationBinding.inflate(inflater, parent, false)

        return Holder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])

    fun submitList(list: List<Location>) {
        items.clear()
        menuOpens.clear()
        items.addAll(list)
        menuOpens.addAll(List(items.size) { false })
        notifyDataSetChanged()
    }

    inner class Holder(private val binding: ItemLocationBinding) : ViewHolder(binding.root) {
        private val swiper = LocationItemMenuSwiper.configure(binding, object : Callback {
            override fun onOpened() {
                menuOpens[layoutPosition] = true
            }

            override fun onClosed() {
                menuOpens[layoutPosition] = false
            }

            override fun onDeleted() {
                AnimUtil.runSpringAnimation(200f, 0f, stiffness = 2500f, dampingRatio = 2f, onEnd = {
                    removeItem(layoutPosition)
                }) {
                    binding.root.alpha = (it / 200f).coerceIn(0f, 1f)
                }
            }
        })

        init {
            binding.deleteContainer setOnDebounceClickListener {
                swiper.deleteMenu()
            }
        }

        fun bind(item: Location) {
            binding.root.alpha = 1f

            if (menuOpens[layoutPosition]) {
                swiper.openMenu(false)
            } else {
                swiper.closeMenu(false)
            }

            binding.item = item
            binding.executePendingBindings()
        }
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        menuOpens.removeAt(position)
        notifyItemRemoved(position)
    }
}

@BindingAdapter("app:recyclerview_")
fun RecyclerView.setItems(items: List<Location>?) {
    if (items == null) return
    (adapter as? SearchAdapter)?.run {
        submitList(items)
    }
}
