package team.weathy.ui.record.start

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import team.weathy.databinding.FragmentRecordStartBinding
import team.weathy.ui.main.MainActivity
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener

class RecordStartFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordStartBinding>()

    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordStartBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureStartNavigation()
    }

    private fun configureStartNavigation() {
        binding.close setOnDebounceClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
        }
        binding.btnChange setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateStartToLocationChange()
        }
        binding.btnStart setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateStartToClothesSelect()
        }
    }
}