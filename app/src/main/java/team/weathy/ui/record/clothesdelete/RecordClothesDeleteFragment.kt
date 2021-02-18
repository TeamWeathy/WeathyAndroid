package team.weathy.ui.record.clothesdelete

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.databinding.FragmentRecordClothesDeleteBinding
import team.weathy.dialog.CommonDialog
import team.weathy.model.entity.WeathyCloth
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.StatusBarUtil
import team.weathy.util.dpFloat
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener

@FlowPreview
class RecordClothesDeleteFragment : Fragment(), CommonDialog.ClickListener {
    private var binding by AutoClearedValue<FragmentRecordClothesDeleteBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesDeleteBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        changeStatusBarColor(getColor(R.color.pink))

        registerBackPressCallback()
        configureClothesDeleteNavigation()
        configureTabs()
        configureChips()
        setButtonActivation()
        showDeleteDialog()
        viewModel.clearSelectedChipsForDelete()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            (activity as? RecordActivity)?.popClothesDelete()
            changeStatusBarColor(getColor(R.color.main_mint))
        }
    }

    private fun registerBackPressCallback() =
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

    private fun configureClothesDeleteNavigation() {
        binding.cancel setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesDelete()
            changeStatusBarColor(getColor(R.color.main_mint))
        }
        binding.delete setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesDelete()
            changeStatusBarColor(getColor(R.color.main_mint))
        }
    }

    private fun changeStatusBarColor(color: Int) {
        StatusBarUtil.changeColor(context as Activity, color)
    }

    private val layouts
        get() = listOf(binding.layoutTop, binding.layoutBottom, binding.layoutOuter, binding.layoutEtc)

    private val category
        get() = listOf(binding.tvTop, binding.tvBottom, binding.tvOuter, binding.tvEtc)

    private val count
        get() = listOf(binding.tvTopCount, binding.tvBottomCount, binding.tvOuterCount, binding.tvEtcCount)

    private fun configureTabs() {
        for (i in 0..3) {
            (layouts[i].getChildAt(1) as? TextView)?.apply {
                text = viewModel.clothesTriple[i].first.value!!.size.toString()
                setTextColor(getColor(R.color.sub_grey_6))
            }
        }
        setOnTabClickListeners(layouts)
        setOnTabClickListeners(category)
        setOnTabClickListeners(count)
        viewModel.choicedClothesTabIndex.observe(viewLifecycleOwner) { tab ->
            selectTab(tab)
        }

        (layouts[viewModel.choicedClothesTabIndex.value!!].getChildAt(1) as? TextView)?.run {
            text = viewModel.clothes.value!!.size.toString()
            setTextColor(getColor(R.color.sub_grey_6))
        }
    }

    private fun setOnTabClickListeners(list: List<View>) {
        list.forEachIndexed { index, constraintLayout ->
            constraintLayout.setOnClickListener {
                viewModel.changeChoicedClothesTabIndex(index)
                binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
            }
        }
    }

    private fun selectTab(tab: Int) {
        val dividers = listOf(binding.divider, binding.divider2, binding.divider3, binding.divider4)

        dividers.forEachIndexed { index, view ->
            view.isInvisible = index != tab
        }
        category.forEachIndexed { index, textView ->
            if (index == tab) textView.setTextColor(getColor(R.color.main_grey))
            else textView.setTextColor(getColor(R.color.sub_grey_6))
        }
        count.forEachIndexed { index, textView ->
            if (index == tab) textView.setTextColor(getColor(R.color.sub_grey_6))
            else textView.setTextColor(getColor(R.color.sub_grey_3))
        }
    }

    private fun configureChips() {
        viewModel.clothes.observe(viewLifecycleOwner) {
            removeAllChips()
            addChipsForChoicedClothes(it)
        }
        viewModel.selectedClothesForDelete.observe(viewLifecycleOwner) {
            updateChipSelectedState()
        }
    }

    private fun removeAllChips() {
        while (binding.chipGroup.childCount > 0) {
            binding.chipGroup.removeViewAt(0)
        }
    }

    private fun addChipsForChoicedClothes(clothes: List<WeathyCloth>) = clothes.forEach { cloth ->
        binding.chipGroup.addView(createChip(cloth.name))
        binding.chipGroup.startLayoutAnimation()
    }

    private fun createChip(text: String): Chip {
        return (layoutInflater.inflate(R.layout.view_clothes_delete_chip, binding.chipGroup, false) as Chip).apply {
            this.text = text
            layoutParams =
                ChipGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setOnCheckedChangeListener { _, isChecked ->
                chipStrokeWidth = if (isChecked) {
                    viewModel.onChipCheckedForDelete(text)
                    1.5.dpFloat
                } else {
                    viewModel.onChipUncheckedForDelete(text)
                    1.dpFloat
                }
            }
        }
    }

    private fun updateChipSelectedState() {
        binding.chipGroup.children.forEach { view ->
            val chip = view as Chip
            chip.isChecked = isChipSelected(chip.text.toString())
        }
    }

    private fun isChipSelected(name: String) = name in viewModel.selectedClothesForDelete.value!!.map { it.name }

    private fun setButtonActivation() {
        viewModel.selectedClothesForDelete.observe(viewLifecycleOwner) {
            if (viewModel.selectedClothesForDelete.value!!.isNotEmpty()) {
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
                "${viewModel.countAllSelectedClothes()}개 태그를 정말 삭제할까요?",
                "삭제하면 되돌릴 수 없어요.",
                "삭제하기",
                getColor(R.color.pink),
                true).show(childFragmentManager, null)
        }
    }

    override fun onClickYes() {
        lifecycleScope.launchWhenStarted {
            viewModel.deleteClothes()
            (activity as? RecordActivity)?.popClothesDelete()
            StatusBarUtil.changeColor(context as Activity, getColor(R.color.main_mint))
            requireContext().showToast("태그가 삭제되었어요!")
        }
    }
}