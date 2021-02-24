package team.weathy.ui.record.weatherrating

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.databinding.FragmentRecordWeatherRatingBinding
import team.weathy.model.entity.WeatherStamp
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.dpFloat
import team.weathy.util.extensions.enableWithAnim
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener
import team.weathy.view.WeathyCardView

@FlowPreview
class RecordWeatherRatingFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordWeatherRatingBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordWeatherRatingBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureStartNavigation()
        configureTabs()
        configureEditRating()
    }

    private fun configureStartNavigation() {
        binding.back setOnDebounceClickListener {
            (activity as? RecordActivity)?.popWeatherRating()
        }
        binding.btnCheck setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateWeatherRatingToDetail()
        }
        binding.editNext setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateWeatherRatingToDetail()
        }
    }

    private fun configureTabs() {
        setOnTabClickListeners()
        viewModel.selectedWeatherRating.observe(viewLifecycleOwner) { stamp ->
            configureUIWithStamp(stamp)
        }
    }

    private val cvReview
        get() = arrayOf(binding.cvReview, binding.cvReview2, binding.cvReview3, binding.cvReview4, binding.cvReview5)

    private fun setOnTabClickListeners() = cvReview.forEachIndexed { index, constraintLayout ->
        constraintLayout.setOnClickListener {
            viewModel.changeSelectedWeatherRatingIndex(index)
        }
    }

    private fun configureUIWithStamp(_stamp: WeatherStamp?) {
        binding.btnCheck.enableWithAnim(_stamp != null)
        binding.edit.enableWithAnim(_stamp != null)
        if (_stamp != null) {
            binding.editNext.setBackgroundResource(R.drawable.btn_modify_next_active)
            binding.editNext.setTextColor(getColor(R.color.mint_icon))
            binding.editNext.isEnabled = true
        } else {
            binding.editNext.setBackgroundResource(R.drawable.btn_modify_next)
            binding.editNext.setTextColor(getColor(R.color.sub_grey_6))
            binding.editNext.isEnabled = false
        }

        val stamp = _stamp ?: return

        cvReview.forEachIndexed { index, card ->
            if (index == stamp.index) {
                highlightCard(card)
            } else {
                normalizeCard(card)
            }
        }
    }

    private fun highlightCard(cvReview: WeathyCardView) {
        cvReview.elevation = 4.dpFloat
        cvReview.shadowColor = getColor(R.color.main_mint_shadow)
        cvReview.strokeColor = getColor(R.color.main_mint)
        cvReview.strokeWidth = 1.5.dpFloat
    }

    private fun normalizeCard(cvReview: WeathyCardView) {
        cvReview.elevation = 0f
        cvReview.strokeColor = getColor(R.color.sub_grey_7)
        cvReview.strokeWidth = 1.dpFloat
    }

    private fun configureEditRating() {
        if (viewModel.edit) {
            configureModifyBehaviors()
            binding.btnCheck.isVisible = false
            binding.edit.isVisible = true
            binding.editNext.isVisible = true
        } else {
            binding.btnCheck.isVisible = true
            binding.edit.isVisible = false
            binding.editNext.isVisible = false
        }
    }

    private fun configureModifyBehaviors() {
        binding.edit setOnDebounceClickListener {
            submit(true)
        }

        viewModel.onRecordEdited.observe(viewLifecycleOwner) {
            requireContext().showToast("웨디 내용이 수정되었어요!")
            requireActivity().finish()
        }
    }

    private fun submit(includeFeedback: Boolean) = lifecycleScope.launchWhenCreated {
        viewModel.submit(includeFeedback)
    }
}