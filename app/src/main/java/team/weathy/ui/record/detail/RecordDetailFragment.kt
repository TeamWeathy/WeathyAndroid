package team.weathy.ui.record.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentRecordDetailBinding
import team.weathy.util.AutoClearedValue

class RecordDetailFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordDetailBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordDetailBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}