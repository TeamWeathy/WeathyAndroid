package team.weathy.ui.record.weatherrating

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
import team.weathy.ui.record.detail.RecordDetailFragment
import team.weathy.util.AutoClearedValue
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

        binding.btnCheck setOnDebounceClickListener {
            (activity as RecordActivity).replaceFragment(RecordDetailFragment())
        }
    }

    private fun setReviewClickListener(cvReview: kotlin.Array<WeathyCardView>, position: Int, button: Button) {
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
        cvReview.shadowColor = resources.getColor(R.color.main_mint_shadow)
        cvReview.strokeColor = resources.getColor(R.color.main_mint)
    }

    @SuppressLint("ResourceType")
    private fun setButtonEnableListener(button: Button) {
        button.setBackgroundColor(resources.getColor(R.color.main_mint))
        button.isEnabled = true
    }

    private fun setBackgroundDisableListener(cvReview: WeathyCardView) {
        cvReview.disableShadow = true
        cvReview.strokeColor = resources.getColor(R.color.sub_grey_7)
    }
}