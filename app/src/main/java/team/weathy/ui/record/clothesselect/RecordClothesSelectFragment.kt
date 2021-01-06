package team.weathy.ui.record.clothesselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import team.weathy.R
import team.weathy.databinding.FragmentRecordClothesSelectBinding
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.debugE
import team.weathy.util.setOnDebounceClickListener


class RecordClothesSelectFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordClothesSelectBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesSelectBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureClothesSelectNavigation()

        debugE(viewModel.selectedClothes.value)
        configureTabs()
        configureChips()
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
        viewModel.choicedClothesTabIndex.observe(viewLifecycleOwner) { tab ->
            selectTab(tab)
        }
    }

    private val layouts
        get() = listOf(
            binding.layoutTop, binding.layoutBottom, binding.layoutOuter, binding.layoutEtc
        )

    private fun setOnTabClickListeners() = layouts.forEachIndexed { index, constraintLayout ->
        constraintLayout.setOnClickListener {
            viewModel.changeSelectedClothesTabIndex(index)
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

    private fun configureChips() {
        viewModel.clothes.observe(viewLifecycleOwner) {
            removeAllChipsWithoutFirst()
            addChipsForChoicedClothes(it)
        }
        viewModel.selectedClothes.observe(viewLifecycleOwner) {
            updateChipSelectedState()
            updateTabTexts()
        }
    }

    private fun removeAllChipsWithoutFirst() {
        while (binding.chipGroup.childCount > 1) {
            binding.chipGroup.removeViewAt(1)
        }
    }

    private fun addChipsForChoicedClothes(clothes: List<String>) = clothes.forEachIndexed { index, s ->
        binding.chipGroup.addView(createChip(s, index))
        binding.chipGroup.startLayoutAnimation()
    }

    private fun createChip(text: String, index: Int): Chip {
        return (layoutInflater.inflate(R.layout.view_clothes_chip, binding.chipGroup, false) as Chip).apply {
            this.text = text
            layoutParams = ChipGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onChipChecked(index)
                } else {
                    onChipUnchecked(index)
                }
            }
        }
    }

    private fun onChipChecked(index: Int) = viewModel.onChipChecked(index)

    private fun onChipUnchecked(index: Int) = viewModel.onChipUnchecked(index)

    private fun updateChipSelectedState() {
        binding.chipGroup.children.drop(1).forEachIndexed { index, view ->
            val chip = view as Chip
            chip.isChecked = isChipSelected(index)
        }
    }

    private fun updateTabTexts() {
        (layouts[viewModel.choicedClothesTabIndex.value!!].getChildAt(1) as? TextView)?.run {
            text = viewModel.selectedClothes.value!!.size.toString()
        }
    }

    private fun isChipSelected(index: Int) = index in viewModel.selectedClothes.value!!
}
