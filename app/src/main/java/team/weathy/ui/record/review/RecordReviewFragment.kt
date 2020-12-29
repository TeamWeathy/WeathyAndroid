package team.weathy.ui.record.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentRecordReviewBinding
import team.weathy.util.AutoClearedValue

class RecordReviewFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordReviewBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordReviewBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}