package team.weathy.ui.record.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.api.WeatherAPI
import team.weathy.databinding.FragmentRecordStartBinding
import team.weathy.di.Api
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.launchCatch
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject

@AndroidEntryPoint
@FlowPreview
class RecordStartFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordStartBinding>()
    private val viewModel by activityViewModels<RecordStartViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = FragmentRecordStartBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configureStartNavigation()
    }

    private fun configureStartNavigation() {
        binding.close setOnDebounceClickListener {
            requireActivity().finish()
        }
        binding.btnChange setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateStartToLocationChange()
        }
        binding.btnStart setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateStartToClothesSelect()
        }
    }
}