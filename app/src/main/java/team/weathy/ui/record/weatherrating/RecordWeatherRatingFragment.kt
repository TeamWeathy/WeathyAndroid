package team.weathy.ui.record.weatherrating

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.databinding.FragmentRecordWeatherRatingBinding
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
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
        viewModel.selectedWeatherRatingIndex.observe(viewLifecycleOwner) { tab ->
            selectTab(tab)
        }
    }

    private val cvReview
        get() = arrayOf(binding.cvReview, binding.cvReview2, binding.cvReview3, binding.cvReview4, binding.cvReview5)

    private fun setOnTabClickListeners() = cvReview.forEachIndexed { index, constraintLayout ->
        constraintLayout.setOnClickListener {
            viewModel.changeSelectedWeatherRatingIndex(index)
        }
    }

    private fun selectTab(tab: Int) {
        cvReview.forEachIndexed { index, _ ->
            if (index == tab) {
                setBackgroundEnableListener(cvReview[index])
                if (!binding.btnCheck.isEnabled)
                    setButtonEnableListener(binding.btnCheck)
                for (i in cvReview.indices)
                    if (i != index)
                        setBackgroundDisableListener(cvReview[i])
            }
        }
    }

    private fun setBackgroundEnableListener(cvReview:WeathyCardView) {
        cvReview.disableShadow = false
        cvReview.shadowColor = getColor(R.color.main_mint_shadow)
        cvReview.strokeColor = getColor(R.color.main_mint)
        cvReview.strokeWidth = 4.5f
    }

    private fun setButtonEnableListener(button: Button) {
        val colorChangeActive = AnimatorInflater.loadAnimator(context, R.animator.color_change_active_anim) as AnimatorSet
        colorChangeActive.setTarget(button)
        colorChangeActive.start()
        button.isEnabled = true
    }

    private fun setBackgroundDisableListener(cvReview: WeathyCardView) {
        cvReview.disableShadow = true
        cvReview.strokeColor = getColor(R.color.sub_grey_7)
        cvReview.strokeWidth = 3f
    }
}