package team.weathy.ui.main.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import team.weathy.databinding.FragmentCalendarBinding
import team.weathy.ui.main.MainViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.debugE
import team.weathy.util.weekOfMonth

class CalendarFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentCalendarBinding>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<CalendarViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentCalendarBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.mainVm = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.curDate.observe(viewLifecycleOwner) {
            debugE("$it, ${it.weekOfMonth}")
        }
    }
}