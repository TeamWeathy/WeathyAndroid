package team.weathy.ui.record.weatherrating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentRecordWeatherRatingBinding
import team.weathy.util.AutoClearedValue

class RecordWeatherRatingFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordWeatherRatingBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordWeatherRatingBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}