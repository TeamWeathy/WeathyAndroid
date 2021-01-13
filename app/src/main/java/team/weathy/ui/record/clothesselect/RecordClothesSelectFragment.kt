package team.weathy.ui.record.clothesselect

import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.databinding.FragmentRecordClothesSelectBinding
import team.weathy.dialog.EditDialog
import team.weathy.model.entity.WeathyCloth
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.enableWithAnim
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener


@FlowPreview
class RecordClothesSelectFragment : Fragment(), EditDialog.ClickListener {
    private var binding by AutoClearedValue<FragmentRecordClothesSelectBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesSelectBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureClothesSelectNavigation()
        configureTabs()
        configureChips()
        configureAddLogic()
        setButtonActivation()
    }

    private fun configureClothesSelectNavigation() {
        binding.back setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesSelect()
        }
        binding.btnCheck setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateClothesSelectToWeatherRating()
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
            binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
        }
    }

    private fun selectTab(tab: Int) {
        val dividers = listOf(binding.divider, binding.divider2, binding.divider3, binding.divider4)
        val category = listOf(binding.tvTop, binding.tvBottom, binding.tvOuter, binding.tvEtc)
        val count1 = listOf(binding.tvTopCount, binding.tvBottomCount, binding.tvOuterCount, binding.tvEtcCount)
        val count2 = listOf(binding.tvTopCount2, binding.tvBottomCount2, binding.tvOuterCount2, binding.tvEtcCount2)

        dividers.forEachIndexed { index, view ->
            view.isInvisible = index != tab
        }
        category.forEachIndexed { index, textView ->
            if (index == tab) textView.setTextColor(getColor(R.color.main_grey))
            else textView.setTextColor(getColor(R.color.sub_grey_6))
        }
        count1.forEachIndexed { index, textView ->
            if (index == tab) textView.setTextColor(getColor(R.color.sub_grey_6))
            else textView.setTextColor(getColor(R.color.sub_grey_3))
        }
        count2.forEachIndexed { index, textView ->
            if (index == tab) textView.setTextColor(getColor(R.color.sub_grey_6))
            else textView.setTextColor(getColor(R.color.sub_grey_3))
        }
    }

    private fun configureChips() {
        viewModel.clothes.observe(viewLifecycleOwner) {
            removeAllChipsWithoutFirst()
            addChipsForChoicedClothes(it)
            updateChipSelectedState()
            updateTabTexts()
        }
        viewModel.selectedClothes.observe(viewLifecycleOwner) {
            updateChipSelectedState()
            updateTabTexts()
        }
        viewModel.onChipCheckedFailed.observe(viewLifecycleOwner) { cloth ->
            (binding.chipGroup.children.toList().find { chip ->
                (chip as? Chip)?.text.toString() == cloth.name
            } as? Chip)!!.isChecked = false
            showExceedMaximumSelectedToast()
        }
    }


    private fun removeAllChipsWithoutFirst() {
        while (binding.chipGroup.childCount > 1) {
            binding.chipGroup.removeViewAt(1)
        }
    }

    private fun addChipsForChoicedClothes(clothes: List<WeathyCloth>) = clothes.forEach { cloth ->
        binding.chipGroup.addView(createChip(cloth.name))
        binding.chipGroup.startLayoutAnimation()
    }

    @Suppress("DEPRECATION")
    private fun createChip(text: String): Chip {
        return (layoutInflater.inflate(R.layout.view_clothes_select_chip, binding.chipGroup, false) as Chip).apply {
            this.text = text
            layoutParams = ChipGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    viewModel.onChipChecked(text)
                } else {
                    viewModel.onChipUnchecked(text)
                }
            }

            setOnLongClickListener {
                val vib = requireContext().getSystemService(Vibrator::class.java)
                vib.vibrate(100)
                (activity as? RecordActivity)?.navigateClothesSelectToClothesDelete()
                true
            }
        }
    }

    private fun showExceedMaximumSelectedToast() = requireContext().showToast("태그는 카테고리당 5개만 선택할 수 있어요.")

    private fun updateChipSelectedState() {
        binding.chipGroup.children.drop(1).forEach { view ->
            val chip = view as Chip
            chip.isChecked = isChipSelected(chip.text.toString())
        }
    }

    private fun updateTabTexts() {
        layouts.map { it.getChildAt(1) as TextView }.forEachIndexed { idx, textView ->
            val selected = viewModel.clothesTriple[idx].second.value!!

            textView.text = selected.size.toString()
            if (selected.isNotEmpty()) textView.setTextColor(getColor(R.color.mint_icon))
            else textView.setTextColor(getColor(R.color.sub_grey_6))
        }
    }

    private fun isChipSelected(name: String) = name in viewModel.selectedClothes.value!!.map { it.name }

    private fun setButtonActivation() {
        viewModel.isButtonEnabled.observe(viewLifecycleOwner) {
            binding.btnCheck.enableWithAnim(it)
        }
    }

    private fun configureAddLogic() = binding.add setOnDebounceClickListener {
        if (viewModel.clothes.value!!.size >= 50) {
            requireContext().showToast("태그를 추가하려면 기존 태그를 삭제해주세요.")
            return@setOnDebounceClickListener
        }

        when (viewModel.choicedClothesTabIndex.value) {
            0 -> {
                EditDialog.newInstance(
                    "상의 추가하기", viewModel.clothes.value!!.size.toString(), getColor(R.color.main_mint)
                ).show(childFragmentManager, null)
            }
            1 -> {
                EditDialog.newInstance(
                    "하의 추가하기", viewModel.clothes.value!!.size.toString(), getColor(R.color.main_mint)
                ).show(childFragmentManager, null)
            }
            2 -> {
                EditDialog.newInstance(
                    "외투 추가하기", viewModel.clothes.value!!.size.toString(), getColor(R.color.main_mint)
                ).show(childFragmentManager, null)
            }
            3 -> {
                EditDialog.newInstance(
                    "기타 추가하기", viewModel.clothes.value!!.size.toString()
                ).show(childFragmentManager, null)
            }
        }
    }

    override fun onClickYes(text: String) {
        lifecycleScope.launchWhenStarted {
            viewModel.addClothes(text)
            requireContext().showToast("태그가 추가되었어요!")
        }
    }
}
