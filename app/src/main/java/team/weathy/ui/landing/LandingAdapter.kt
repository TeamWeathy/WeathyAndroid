package team.weathy.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import team.weathy.databinding.ItemLandingBinding

class LandingAdapter : RecyclerView.Adapter<LandingAdapter.LandingHolder>() {
    private var items = listOf<Item>()
    fun submitItems(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLandingBinding.inflate(inflater, parent, false)

        return LandingHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: LandingHolder, position: Int) = holder.bind(items[position])


    inner class LandingHolder(private val binding: ItemLandingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    data class Item(
        val title: String
    )
}

@BindingAdapter("landing_items")
fun ViewPager2.submitItems(items: List<LandingAdapter.Item>) {
    (adapter as? LandingAdapter)?.submitItems(items)
}