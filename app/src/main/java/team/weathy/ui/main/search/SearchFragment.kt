package team.weathy.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import reactivecircus.flowbinding.android.widget.textChanges
import team.weathy.databinding.FragmentSearchBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.LinearItemDecoration
import team.weathy.util.setOnDebounceClickListener

@FlowPreview
@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentSearchBinding>()
    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSearchBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configureBackButton()
        configureList()
        configureTextField()
    }

    private fun configureBackButton() = binding.back setOnDebounceClickListener {
        requireActivity().onBackPressed()
    }

    private fun configureList() = binding.list.let { list ->
        list.adapter = SearchAdapter {
            viewModel.onItemRemoved(it)
        }
        list.addItemDecoration(LinearItemDecoration(20))
    }

    private fun configureTextField() = binding.textField.let {
        it.setOnFocusChangeListener { _, hasFocus ->
            viewModel.focused.value = hasFocus
        }

        lifecycleScope.launchWhenStarted {
            it.textChanges().drop(1).debounce(500L).collect {
                viewModel.search()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}