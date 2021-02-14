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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import team.weathy.R
import team.weathy.databinding.FragmentRecordClothesSelectBinding
import team.weathy.dialog.EditDialog
import team.weathy.model.entity.WeathyCloth
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.UniqueIdentifier
import team.weathy.util.extensions.enableWithAnim
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class RecordClothesSelectFragment : Fragment(), EditDialog.ClickListener {
    private var binding by AutoClearedValue<FragmentRecordClothesSelectBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    @Inject
    lateinit var uniqueId: UniqueIdentifier

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesSelectBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNickname()
        configureClothesSelectNavigation()
        configureTabs()
        configureChips()
        configureAddLogic()
        configureButton()
        setButtonActivation()
    }

    private fun setNickname() {
        lifecycleScope.launchWhenStarted {
            uniqueId.userNickname.collect {
                binding.tvNickName.text = "${it}님"
            }
        }
    }

    private fun configureClothesSelectNavigation() {
        binding.back setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesSelect()
        }
        binding.btnCheck setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateClothesSelectToWeatherRating()
        }
        binding.editNext setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateClothesSelectToWeatherRating()
        }
        binding.delete setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateClothesSelectToClothesDelete()
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
            viewModel.changeChoicedClothesTabIndex(index)
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
            else textView.setTextColor(getColor(R.color.sub_grey_4))
        }
        count2.forEachIndexed { index, textView ->
            if (index == tab) textView.setTextColor(getColor(R.color.sub_grey_6))
            else textView.setTextColor(getColor(R.color.sub_grey_4))
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
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.onChipChecked(text)
                } else {
                    viewModel.onChipUnchecked(text)
                }
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
            when (viewModel.choicedClothesTabIndex.value == idx) {
                true -> {
                    if (selected.isNotEmpty()) textView.setTextColor(getColor(R.color.mint_icon))
                    else textView.setTextColor(getColor(R.color.sub_grey_6))
                }
                false -> textView.setTextColor(getColor(R.color.sub_grey_4))
            }
        }
    }

    private fun isChipSelected(name: String) = name in viewModel.selectedClothes.value!!.map { it.name }

    private fun setButtonActivation() {
        viewModel.isButtonEnabled.observe(viewLifecycleOwner) {
            binding.btnCheck.enableWithAnim(it)
            binding.edit.enableWithAnim(it)
            if (viewModel.isButtonEnabled.value!!) {
                binding.editNext.setBackgroundResource(R.drawable.btn_modify_next_active)
                binding.editNext.setTextColor(getColor(R.color.mint_icon))
                binding.editNext.isEnabled = true
            } else {
                binding.editNext.setBackgroundResource(R.drawable.btn_modify_next)
                binding.editNext.setTextColor(getColor(R.color.sub_grey_6))
                binding.editNext.isEnabled = false
            }
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
                    "상의 추가하기", getColor(R.color.main_mint)).show(childFragmentManager, null)
            }
            1 -> {
                EditDialog.newInstance(
                    "하의 추가하기", getColor(R.color.main_mint)).show(childFragmentManager, null)
            }
            2 -> {
                EditDialog.newInstance(
                    "외투 추가하기", getColor(R.color.main_mint)).show(childFragmentManager, null)
            }
            3 -> {
                EditDialog.newInstance(
                    "기타 추가하기", getColor(R.color.main_mint)).show(childFragmentManager, null)
            }
        }
    }

    private fun configureButton() {
        if (viewModel.edit) {
            binding.btnCheck.isVisible = false
            binding.edit.isVisible = true
            binding.editNext.isVisible = true
        } else {
            binding.btnCheck.isVisible = true
            binding.edit.isVisible = false
            binding.editNext.isVisible = false
        }
    }

    override fun onClickYes(text: String) {
        lifecycleScope.launchWhenStarted {
            if (viewModel.addClothes(text))
                requireContext().showToast("'${text}' 태그가 추가되었어요!")
            else
                requireContext().showToast("이미 있는 옷은 또 등록할 수 없어요.")
        }
    }
}