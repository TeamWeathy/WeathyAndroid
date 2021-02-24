package team.weathy.ui.main.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.databinding.FragmentCalendarBinding
import team.weathy.dialog.CommonDialog
import team.weathy.dialog.DateDialog
import team.weathy.dialog.DateDialog.OnClickListener
import team.weathy.ui.main.MainMenu.HOME
import team.weathy.ui.main.MainViewModel
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.showToast
import team.weathy.util.isSameDay
import team.weathy.util.setOnDebounceClickListener
import java.time.LocalDate
import java.time.LocalDateTime

@FlowPreview
@AndroidEntryPoint
class CalendarFragment : Fragment(), OnClickListener, CommonDialog.ClickListener {
    private var binding by AutoClearedValue<FragmentCalendarBinding>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by activityViewModels<CalendarViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (viewModel.isMoreMenuShowing.value == true) {
                viewModel.closeMoreMenu()
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

        setOnRecordClickListener()

        viewModel.onDeleteSuccess.observe(viewLifecycleOwner) {
            requireContext().showToast("웨디가 삭제되었어요.")
        }
        viewModel.onDeleteFailed.observe(viewLifecycleOwner) {
            requireContext().showToast("웨디가 삭제가 실패했어요.")
        }

        binding.edit setOnDebounceClickListener {
            viewModel.closeMoreMenu()
            navigateRecordAtCurDate(true)
        }
        binding.delete setOnDebounceClickListener {
            showDeleteWeathyDialog()
        }
    }

    private fun configureCalendarView() {
        binding.calendarView.run {
            onClickYearMonthText = {
                DateDialog.newInstance(binding.calendarView.curDate).show(childFragmentManager, null)
            }

            onDateChangeListener = {
                if (!viewModel.curDate.value!!.isSameDay(it)) {
                    viewModel.onCurDateChanged(it)
                }
            }

            onSelectedDateChangeListener = {
                if (!viewModel.selectedDate.value!!.isSameDay(it)) {
                    viewModel.onSelectedDateChanged(it)
                }
            }
        }

        viewModel.curDate.observe(viewLifecycleOwner) {
            binding.calendarView.curDate = it

        }

        viewModel.selectedDate.observe(viewLifecycleOwner) {
            binding.calendarView.selectedDate = it
            binding.scrollView.smoothScrollTo(0, 0)
        }

        viewModel.curWeathy.observe(viewLifecycleOwner) {
            binding.container.startLayoutAnimation()
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
        binding.calendarView.selectedDate = date
    }

    private fun setOnRecordClickListener() = binding.record setOnDebounceClickListener {
        navigateRecordAtCurDate(false)
    }

    private fun navigateRecordAtCurDate(edit: Boolean) {
        val selectedDate = viewModel.selectedDate.value!!
        RecordViewModel.lastRecordNavigationTime = selectedDate.atTime(LocalDateTime.now().hour, 0)
        if (edit) {
            RecordViewModel.lastEditWeathy = viewModel.curWeathy.value
        }
        startActivity(RecordActivity.newIntent(requireContext(), edit))
    }

    private fun showDeleteWeathyDialog() {
        CommonDialog.newInstance(
            "이 날의 웨디를 삭제할까요?",
            "삭제하면 되돌릴 수 없어요.",
            "삭제하기",
            "취소",
            getColor(R.color.pink),
            true).show(childFragmentManager, null)
    }

    override fun onClickYes() {
        lifecycleScope.launchWhenStarted {
            viewModel.delete()
        }
    }
}