package team.weathy.ui.record.weatherrating

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import team.weathy.R
import team.weathy.databinding.FragmentRecordWeatherRatingBinding
import team.weathy.ui.record.RecordActivity
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.getColor
import team.weathy.util.setOnDebounceClickListener
import team.weathy.view.WeathyCardView

class RecordWeatherRatingFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordWeatherRatingBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordWeatherRatingBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cvReview = arrayOf(binding.cvReview, binding.cvReview2, binding.cvReview3, binding.cvReview4, binding.cvReview5)

        for (i in cvReview.indices)
            setReviewClickListener(cvReview, i, binding.btnCheck)

        configureStartNavigation()
    }

    private fun configureStartNavigation() {
        binding.back setOnDebounceClickListener {
            (activity as? RecordActivity)?.popWeatherRating()
        }
        binding.btnCheck setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateWeatherRatingToDetail()
        }
    }

    private fun setReviewClickListener(cvReview: Array<WeathyCardView>, position: Int, button: Button) {
        cvReview[position] setOnDebounceClickListener {
            setBackgroundEnableListener(cvReview[position])
            setButtonEnableListener(button)
            for (i in cvReview.indices)
                if (i != position)
                    setBackgroundDisableListener(cvReview[i])
        }
    }

    private fun setBackgroundEnableListener(cvReview:WeathyCardView ) {
        cvReview.disableShadow = false
        cvReview.shadowColor = getColor(R.color.main_mint_shadow)
        cvReview.strokeColor = getColor(R.color.main_mint)
    }

    @SuppressLint("ResourceType")
    private fun setButtonEnableListener(button: Button) {
        val colorChangeActive = AnimatorInflater.loadAnimator(context, R.animator.color_change_active_anim) as AnimatorSet
        colorChangeActive.setTarget(button)
        colorChangeActive.start()
        button.isEnabled = true
    }

    private fun setBackgroundDisableListener(cvReview: WeathyCardView) {
        cvReview.disableShadow = true
        cvReview.strokeColor = getColor(R.color.sub_grey_7)
    }
}