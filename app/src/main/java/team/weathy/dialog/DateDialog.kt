package team.weathy.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import team.weathy.R
import team.weathy.databinding.DialogDateBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener
import java.time.LocalDate
import java.util.*

class DateDialog : BottomSheetDialogFragment() {
    private var binding by AutoClearedValue<DialogDateBinding>()

    private val date: LocalDate
        get() = arguments?.getSerializable("date") as? LocalDate ?: LocalDate.now()
    private val listener: OnClickListener?
        get() = (parentFragment as? OnClickListener) ?: (activity as? OnClickListener)

    override fun getTheme() = R.style.RoundBottomSheetDialog
    override fun onCreateDialog(savedInstanceState: Bundle?) = BottomSheetDialog(requireActivity(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DialogDateBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.picker.let {
            it.maxDate = Calendar.getInstance().timeInMillis
            it.minDate = Calendar.getInstance().apply {
                set(2020, 11, 13)
            }.timeInMillis
            it.init(date.year, date.monthValue - 1, date.dayOfMonth) { _, _, _, _ ->

            }
        }

        binding.close setOnDebounceClickListener {
            dismiss()
        }
        binding.select setOnDebounceClickListener {
            listener?.onClick(
                LocalDate.of(
                    binding.picker.year,
                    binding.picker.month + 1,
                    binding.picker.dayOfMonth,
                )
            )
            dismiss()
        }
    }

    interface OnClickListener {
        fun onClick(date: LocalDate) {}
    }

    companion object {
        fun newInstance(date: LocalDate) = DateDialog().apply {
            arguments = bundleOf("date" to date)
        }
    }
}