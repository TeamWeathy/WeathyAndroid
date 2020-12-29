package team.weathy.ui.record.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentRecordStartBinding
import team.weathy.util.AutoClearedValue

class RecordStartFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordStartBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordStartBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}