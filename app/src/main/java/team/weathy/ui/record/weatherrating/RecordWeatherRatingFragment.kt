package team.weathy.ui.record.weatherrating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    }

    private fun configureStartNavigation() {
        binding.back setOnDebounceClickListener {
            (activity as? RecordActivity)?.popWeatherRating()
        }
        binding.btnCheck setOnDebounceClickListener {
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
}