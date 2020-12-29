package team.weathy.ui.record.clothesdelete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentRecordClothesDeleteBinding
import team.weathy.util.AutoClearedValue

class RecordClothesDeleteFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordClothesDeleteBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesDeleteBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}