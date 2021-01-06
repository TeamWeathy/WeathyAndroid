package team.weathy.ui.record.clothesselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import bolts.Bolts
import com.google.android.material.chip.Chip
import team.weathy.R
import team.weathy.databinding.FragmentRecordClothesSelectBinding
import team.weathy.dialog.EditDialog
import team.weathy.ui.record.RecordActivity
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.addFragment
import team.weathy.util.extensions.getColor
import team.weathy.util.setOnDebounceClickListener
import team.weathy.view.WeathyCardView


class RecordClothesSelectFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordClothesSelectBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordClothesSelectBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureClothesSelectNavigation()
    }

    private fun configureClothesSelectNavigation() {
        binding.btnCheck setOnDebounceClickListener {
            (activity as? RecordActivity)?.navigateClothesSelectToWeatherRating()
        }
        binding.add.setOnLongClickListener {
            (activity as? RecordActivity)?.navigateClothesSelectToClothesDelete()
            true
        }
    }
}
