package team.weathy.ui.main.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.databinding.FragmentCalendarBinding
import team.weathy.dialog.DateDialog
import team.weathy.dialog.DateDialog.OnClickListener
import team.weathy.ui.main.MainMenu.CALENDAR
import team.weathy.ui.main.MainMenu.HOME
import team.weathy.ui.main.MainViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.view.calendar.CalendarView
import java.time.LocalDate

@AndroidEntryPoint
class CalendarFragment : Fragment(), OnClickListener {
    private var binding by AutoClearedValue<FragmentCalendarBinding>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<CalendarViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (viewModel.isMoreMenuShowing.value == true) {
                viewModel.onClickMoreMenu()
            } else {
                mainViewModel.changeMenu(HOME)
                isEnabled = false
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentCalendarBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.mainVm = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configureCalendarView()

        registerBackPressCallback()
    }

    private fun configureCalendarView() {
        binding.calendarView.run {
            onClickYearMonthText = {
                DateDialog.newInstance(binding.calendarView.curDate).show(childFragmentManager, null)
            }

            onDateChangeListener = CalendarView.OnDateChangeListener {
                viewModel.onCurDateChanged(it)
            }
        }

        viewModel.curDate.observe(viewLifecycleOwner) {
            binding.calendarView.curDate = it
            binding.container.startLayoutAnimation()
            binding.scrollView.smoothScrollTo(0, 0)
        }

        viewModel.calendarData.observe(viewLifecycleOwner) {
            binding.calendarView.data = it
        }
    }

    private fun registerBackPressCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        onBackPressedCallback.isEnabled = true
    }

    override fun onClick(date: LocalDate) {
        binding.calendarView.curDate = date
    }
}