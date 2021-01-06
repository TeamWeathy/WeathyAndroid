package team.weathy.ui.record.clothesselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import team.weathy.databinding.FragmentRecordClothesSelectBinding
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener


class RecordClothesSelectFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordClothesSelectBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesSelectBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureClothesSelectNavigation()

        configureTabs()
    }

    private fun configureClothesSelectNavigation() {
        binding.back setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesSelect()
        }
        binding.btnCheck setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateClothesSelectToWeatherRating()
        }
        binding.add.setOnLongClickListener {
            (activity as? RecordActivity)?.navigateClothesSelectToClothesDelete()
            true
        }
    }

    private fun configureTabs() {
        setOnTabClickListeners()
        viewModel.selectedClothesTabIndex.observe(viewLifecycleOwner) { tab ->
            selectTab(tab)
        }
    }

    private fun setOnTabClickListeners() {
        binding.layoutTop setOnDebounceClickListener {
            viewModel.changeSelectedClothesTabIndex(0)
        }
        binding.layoutBottom setOnDebounceClickListener {
            viewModel.changeSelectedClothesTabIndex(1)
        }
        binding.layoutOuter setOnDebounceClickListener {
            viewModel.changeSelectedClothesTabIndex(2)
        }
        binding.layoutEtc setOnDebounceClickListener {
            viewModel.changeSelectedClothesTabIndex(3)
        }
    }

    private fun selectTab(tab: Int) {
        val dividers = listOf(
            binding.divider,
            binding.divider2,
            binding.divider3,
            binding.divider4,
        )
        dividers.forEachIndexed { index, view ->
            view.isInvisible = index != tab
        }
    }
}
