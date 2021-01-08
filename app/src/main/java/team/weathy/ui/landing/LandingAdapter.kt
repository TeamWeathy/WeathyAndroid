package team.weathy.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import team.weathy.databinding.ItemLandingBinding
import team.weathy.ui.landing.LandingActivity.Companion.ITEM_COUNT

class LandingAdapter : RecyclerView.Adapter<LandingAdapter.LandingHolder>() {
    private val items = Item.defaults

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLandingBinding.inflate(inflater, parent, false)

        return LandingHolder(binding)
    }

    override fun getItemCount() = ITEM_COUNT

    override fun onBindViewHolder(holder: LandingHolder, position: Int) = holder.bind(items[position])

    inner class LandingHolder(private val binding: ItemLandingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    data class Item(
        val titleNormal: String,
        val titleHighlight: String,
        val description: String,
    ) {
        companion object {
            val defaults = listOf(
                Item(
                    "날씨를",
                    "기록해요",
                    "오늘 날씨에 대한 옷차림과 상태를 기록해요",
                ),
                Item(
                    "기록을",
                    "모아봐요",
                    "캘린더에서 날씨 기록을 모아볼 수 있어요",
                ),
                Item(
                    "나에게",
                    "돌아와요",
                    "기록한 날씨는 비슷한 날에 돌아와요",
                ),
            )
        }
    }
}