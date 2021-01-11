package team.weathy.ui.record.clothesselect

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.os.Vibrator
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
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.databinding.FragmentRecordClothesSelectBinding
import team.weathy.dialog.EditDialog
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
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
        }
        viewModel.selectedClothes.observe(viewLifecycleOwner) {
            updateChipSelectedState()
            updateTabTexts()
        }
        viewModel.onChipCheckedFailed.observe(viewLifecycleOwner) {
            (binding.chipGroup.children.toList().getOrNull(it) as? Chip)?.isChecked = false
            showExceedMaximumSelectedToast()
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

    @Suppress("DEPRECATION")
    private fun createChip(text: String, index: Int): Chip {
        return (layoutInflater.inflate(R.layout.view_clothes_select_chip, binding.chipGroup, false) as Chip).apply {
            this.text = text
            layoutParams = ChipGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setOnCheckedChangeListener { button, isChecked ->
                if (viewModel.selectedClothes.value!!.size == 5 && button.isChecked) {
                    button.isChecked = false
                    showExceedMaximumSelectedToast()
                    return@setOnCheckedChangeListener
                }

                if (isChecked) {
                    viewModel.onChipChecked(index)
                } else {
                    viewModel.onChipUnchecked(index)
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
        binding.chipGroup.children.drop(1).forEachIndexed { index, view ->
            val chip = view as Chip
            chip.isChecked = isChipSelected(index)
        }
    }

    private fun updateTabTexts() {
        (layouts[viewModel.choicedClothesTabIndex.value!!].getChildAt(1) as? TextView)?.run {
            text = viewModel.selectedClothes.value!!.size.toString()
            if (viewModel.selectedClothes.value!!.isNotEmpty()) setTextColor(getColor(R.color.mint_icon))
            else setTextColor(getColor(R.color.sub_grey_6))
        }
    }

    private fun isChipSelected(index: Int) = index in viewModel.selectedClothes.value!!

    private fun setButtonActivation() {
        viewModel.selectedClothes.observe(viewLifecycleOwner) {
            if (viewModel.selectedClothes.value!!.isNotEmpty()) {
                setButtonEnabled(true)
            } else {
                setButtonDisabled(false)
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
        viewModel.addClothes(text)
    }

    private fun setButtonEnabled(isEnable: Boolean) {
        val colorChangeActive = AnimatorInflater.loadAnimator(context, R.animator.color_change_active_anim) as AnimatorSet
        colorChangeActive.setTarget(binding.btnCheck)
        colorChangeActive.start()
        binding.btnCheck.isEnabled = isEnable
    }

    private fun setButtonDisabled(isEnable: Boolean) {
        val colorChange = AnimatorInflater.loadAnimator(context, R.animator.color_change_anim) as AnimatorSet
        colorChange.setTarget(binding.btnCheck)
        colorChange.start()
        binding.btnCheck.isEnabled = isEnable
    }
}
