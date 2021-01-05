package team.weathy.ui.main

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import team.weathy.ui.main.calendar.CalendarFragment
import team.weathy.ui.main.calendar.HomeFragment
import team.weathy.ui.main.search.SearchFragment

class MainFragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int) = when (position) {
        0 -> HomeFragment()
        1 -> CalendarFragment()
        2 -> SearchFragment()
        else -> throw IllegalArgumentException()
    }
}