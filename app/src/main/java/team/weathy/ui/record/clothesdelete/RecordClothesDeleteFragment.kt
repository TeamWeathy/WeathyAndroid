package team.weathy.ui.record.clothesdelete

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import team.weathy.R
import team.weathy.databinding.FragmentRecordClothesDeleteBinding
import team.weathy.dialog.CommonDialog
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.StatusBarUtil
import team.weathy.util.extensions.getColor
import team.weathy.util.setOnDebounceClickListener
import kotlin.math.roundToInt

class RecordClothesDeleteFragment : Fragment(), CommonDialog.ClickListener {
    private var binding by AutoClearedValue<FragmentRecordClothesDeleteBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesDeleteBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        StatusBarUtil.changeColor(context as Activity, getColor(R.color.pink))

        configureClothesDeleteNavigation()
        configureTabs()
        configureChips()
        setButtonActivation()
        showDeleteDialog()
    }

    private fun configureClothesDeleteNavigation() {
        binding.cancel setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesDelete()
            StatusBarUtil.changeColor(context as Activity, getColor(R.color.main_mint))
        }
        binding.delete setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesDelete()
            StatusBarUtil.changeColor(context as Activity, getColor(R.color.main_mint))
        }
    }

    private val layouts
        get() = listOf(
            binding.layoutTop, binding.layoutBottom, binding.layoutOuter, binding.layoutEtc
        )

    private fun configureTabs() {
        for (i in 0..3) {
            (layouts[i].getChildAt(1) as? TextView)?.apply {
                text = viewModel.clothesPairs[i].first.value!!.size.toString()
                setTextColor(getColor(R.color.sub_grey_6))
            }
        }
        setOnTabClickListeners()
        viewModel.choicedClothesTabIndex.observe(viewLifecycleOwner) { tab ->
            selectTab(tab)
        }

        (layouts[viewModel.choicedClothesTabIndex.value!!].getChildAt(1) as? TextView)?.run {
            text = viewModel.clothes.value!!.size.toString()
            setTextColor(getColor(R.color.sub_grey_6))
        }
    }

    private fun setOnTabClickListeners() = layouts.forEachIndexed { index, constraintLayout ->
        constraintLayout.setOnClickListener {
            viewModel.changeSelectedClothesTabIndex(index)
        }
    }

    private fun selectTab(tab: Int) {
        val dividers = listOf(binding.divider, binding.divider2, binding.divider3, binding.divider4)
        val category = listOf(binding.tvTop, binding.tvBottom, binding.tvOuter, binding.tvEtc)
        val count = listOf(binding.tvTopCount, binding.tvBottomCount, binding.tvOuterCount, binding.tvEtcCount)

        dividers.forEachIndexed { index, view ->
            view.isInvisible = index != tab
        }
        category.forEachIndexed { index, textView ->
            if (index == tab)
                textView.setTextColor(getColor(R.color.main_grey))
            else
                textView.setTextColor(getColor(R.color.sub_grey_6))
        }
        count.forEachIndexed { index, textView ->
            if (index == tab)
                textView.setTextColor(getColor(R.color.sub_grey_6))
            else
                textView.setTextColor(getColor(R.color.sub_grey_3))
        }
    }

    private fun configureChips() {
        viewModel.clothes.observe(viewLifecycleOwner) {
            removeAllChips()
            addChipsForChoicedClothes(it)
        }
        viewModel.selectedClothes.observe(viewLifecycleOwner) {
            updateChipSelectedState()
        }
    }

    private fun removeAllChips() {
        while (binding.chipGroup.childCount > 0) {
            binding.chipGroup.removeViewAt(0)
        }
    }

    private fun addChipsForChoicedClothes(clothes: List<String>) = clothes.forEachIndexed { index, s ->
        binding.chipGroup.addView(createChip(s, index))
        binding.chipGroup.startLayoutAnimation()
    }

    private fun createChip(text: String, index: Int): Chip {
        return (layoutInflater.inflate(R.layout.view_clothes_delete_chip, binding.chipGroup, false) as Chip).apply {
            this.text = text
            layoutParams = ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onChipCheckedForDelete(index)
                    chipStrokeWidth = 4.5f
                } else {
                    onChipUnchecked(index)
                    chipStrokeWidth = 3f
                }
            }
        }
    }

    private fun onChipCheckedForDelete(index: Int) = viewModel.onChipCheckedForDelete(index)

    private fun onChipUnchecked(index: Int) = viewModel.onChipUnchecked(index)

    private fun updateChipSelectedState() {
        binding.chipGroup.children.forEachIndexed { index, view ->
            val chip = view as Chip
            chip.isChecked = isChipSelected(index)
        }
    }

    private fun isChipSelected(index: Int) = index in viewModel.selectedClothes.value!!

    private fun setButtonActivation() {
        viewModel.selectedClothes.observe(viewLifecycleOwner) {
            if (viewModel.selectedClothes.value!!.isNotEmpty()) {
                binding.delete.isEnabled = true
                binding.delete.setBackgroundColor(getColor(R.color.pink))
            } else {
                binding.delete.isEnabled = false
                binding.delete.setBackgroundColor(getColor(R.color.sub_grey_3))
            }
        }
    }

    private fun showDeleteDialog() {
        binding.delete setOnDebounceClickListener {
            CommonDialog.newInstance(
                "${viewModel.allSelectedClothes()}개 태그를 정말 삭제할까요?",
                "삭제하면 되돌릴 수 없어요.",
                "삭제하기",
                getColor(R.color.pink),
                true)
                .show(childFragmentManager, null)
        }
    }

    override fun onClickYes() {
        viewModel.selectedClothes.observe(viewLifecycleOwner) {

        }
        (activity as? RecordActivity)?.popClothesDelete()
        StatusBarUtil.changeColor(context as Activity, getColor(R.color.main_mint))
    }
}