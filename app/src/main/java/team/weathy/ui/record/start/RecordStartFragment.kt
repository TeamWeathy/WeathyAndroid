package team.weathy.ui.record.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.databinding.FragmentRecordStartBinding
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.font
import team.weathy.util.extensions.getColor
import team.weathy.util.monthDayFormat
import team.weathy.util.setOnDebounceClickListener

@AndroidEntryPoint
@FlowPreview
class RecordStartFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordStartBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = FragmentRecordStartBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configureStartNavigation()
        configureDate()
        configureEditStart()
    }

    private fun configureStartNavigation() {
        binding.close setOnDebounceClickListener {
            requireActivity().finish()
        }
        binding.btnChange setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateStartToLocationChange()
        }
        binding.btnStart setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateStartToClothesSelect()
        }
        binding.editNext setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateStartToClothesSelect()
        }
    }

    private fun configureDate() {
        binding.date.text = buildSpannedString {
            append("${viewModel.date.toLocalDate().monthDayFormat}의 ")

            color(getColor(R.color.mint_icon)) {
                font(ResourcesCompat.getFont(requireContext(), R.font.notosans_medium)) {
                    append("웨디")
                }
            }

            append("를\n${if (viewModel.edit) "수정" else "기록"}해볼까요?")
        }
    }

    private fun configureEditStart() {
        if (viewModel.edit) {
            binding.btnStart.isVisible = false
            binding.edit.isVisible = true
            binding.editNext.isVisible = true
        } else {
            binding.btnStart.isVisible = true
            binding.edit.isVisible = false
            binding.editNext.isVisible = false
        }
    }
}