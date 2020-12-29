package team.weathy.ui.record.locationchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentRecordLocationChangeBinding
import team.weathy.util.AutoClearedValue

class RecordLocationChangeFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordLocationChangeBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordLocationChangeBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}