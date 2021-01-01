package team.weathy.ui.main.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import team.weathy.databinding.FragmentCalendarBinding
import team.weathy.ui.main.MainViewModel
import team.weathy.util.AutoClearedValue

class CalendarFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentCalendarBinding>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentCalendarBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = mainViewModel
//        binding.container.startLayoutAnimation()
    }
}