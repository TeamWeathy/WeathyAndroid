package team.weathy.ui.record.clothesselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import team.weathy.databinding.FragmentRecordClothesSelectBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener


class RecordClothesSelectFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordClothesSelectBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            FragmentRecordClothesSelectBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chipPlus()

    }

    private fun chipPlus() {
        binding.chipPlus.setOnDebounceClickListener {
            val chip = Chip(this.context)
            chip.setText("chip")
            binding.chipGroup.addView(chip)
        }
    }

}