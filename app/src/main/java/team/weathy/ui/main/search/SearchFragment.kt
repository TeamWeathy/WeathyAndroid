package team.weathy.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentSearchBinding
import team.weathy.model.entity.Location
import team.weathy.util.AutoClearedValue
import team.weathy.util.LinearItemDecoration
import team.weathy.util.setOnDebounceClickListener

class SearchFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentSearchBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSearchBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureBackButton()
        configureList()
    }

    private fun configureBackButton() = binding.back setOnDebounceClickListener {
        requireActivity().onBackPressed()
    }

    private fun configureList() = binding.list.let { list ->
        list.adapter = SearchAdapter().apply {
            submitList(
                listOf(
                    Location(1),
                    Location(1),
                    Location(1),
                    Location(1),
                    Location(1),
                    Location(1),
                )
            )
        }
        list.addItemDecoration(LinearItemDecoration(20))
    }
}