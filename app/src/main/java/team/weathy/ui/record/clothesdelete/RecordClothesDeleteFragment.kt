package team.weathy.ui.record.clothesdelete

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.R
import team.weathy.databinding.FragmentRecordClothesDeleteBinding
import team.weathy.ui.record.RecordActivity
import team.weathy.util.AutoClearedValue
import team.weathy.util.StatusBarUtil
import team.weathy.util.extensions.getColor
import team.weathy.util.setOnDebounceClickListener

class RecordClothesDeleteFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordClothesDeleteBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesDeleteBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        StatusBarUtil.changeColor(context as Activity, getColor(R.color.pink))

        configureClothesDeleteNavigation()
    }

    private fun configureClothesDeleteNavigation() {
        binding.cancel setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesDelete()
            StatusBarUtil.changeColor(context as Activity, getColor(R.color.main_mint))
        }
        binding.delete setOnDebounceClickListener {
            (activity as? RecordActivity)?.popClothesDelete()
            StatusBarUtil.changeColor(context as Activity, getColor(R.color.main_mint))
        }
    }
}