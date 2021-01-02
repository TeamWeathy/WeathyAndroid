package team.weathy.ui.record.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import team.weathy.databinding.FragmentRecordStartBinding
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener

class RecordStartFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordStartBinding>()

    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordStartBinding.inflate(layoutInflater, container, false).also {
            binding = it
            it.btnChange setOnDebounceClickListener {
                (activity as? RecordActivity)?.navigateStartToLocationChange()
            }
            it.btnStart setOnDebounceClickListener {
                (activity as? RecordActivity)?.navigateStartToClothesSelect()
            }
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}