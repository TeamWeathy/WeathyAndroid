package team.weathy.ui.main.home

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import team.weathy.model.entity.HourlyWeather
import team.weathy.util.SimpleEventLiveData
import team.weathy.view.HourlyWeatherView

class HomeHourlyPagerAdapter(
    private val onResetAnimation: SimpleEventLiveData,
    private val onStartAnimation: SimpleEventLiveData,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<HomeHourlyPagerAdapter.HomeHourlyPagerHolder>() {
    private var items: List<HourlyWeather> = listOf()
    fun submitList(items: List<HourlyWeather>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHourlyPagerHolder {
        return HomeHourlyPagerHolder(HourlyWeatherView(parent.context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
    }

    override fun getItemCount() = 4

    override fun onBindViewHolder(holder: HomeHourlyPagerHolder, position: Int) {
        val subList = try {
            items.subList(position * 7, (position + 1) * 7)
        } catch (e: Throwable) {
            listOf()
        }
        holder.bind(subList)
    }


    inner class HomeHourlyPagerHolder(private val view: HourlyWeatherView) : RecyclerView.ViewHolder(view) {
        init {
            onResetAnimation.observe(lifecycleOwner) {
                view.resetAnimation()
            }
            onStartAnimation.observe(lifecycleOwner) {
                view.startAnimation()
            }
        }

        fun bind(item: List<HourlyWeather>) {
            view.weathers = item
            view.position = layoutPosition
        }
    }
}

@BindingAdapter("hourlyWeathers")
fun ViewPager2.setHourlyWeather(weathers: List<HourlyWeather>?) {
    weathers ?: return
    (adapter as? HomeHourlyPagerAdapter)?.submitList(weathers)
}