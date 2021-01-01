package team.weathy.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentSearchBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener

class SearchFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentSearchBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSearchBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureBackButton()
    }

    private fun configureBackButton() = binding.back setOnDebounceClickListener {
        requireActivity().onBackPressed()
    }
}