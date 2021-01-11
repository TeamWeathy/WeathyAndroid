package team.weathy.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.databinding.FragmentSearchBinding
import team.weathy.ui.main.MainMenu.HOME
import team.weathy.ui.main.MainMenu.SEARCH
import team.weathy.ui.main.MainViewModel
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.LinearItemDecoration
import team.weathy.util.setOnDebounceClickListener

@FlowPreview
@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val fromRecord
        get() = arguments?.getBoolean("fromRecord") ?: false
    private var binding by AutoClearedValue<FragmentSearchBinding>()
    private val viewModel by viewModels<SearchViewModel>()

    /**
     * This ViewModel is not initialized when fromRecord == true
     */
    private val mainViewModel by activityViewModels<MainViewModel>()

    /**
     * This ViewModel is not initialized when fromRecord == false or null
     */
    private val recordViewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSearchBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configureBackButton()
        configureList()
        configureTextFieldSearch()

        if (!fromRecord) {
            registerBackPressCallback()
            handleMainMenuChange()
        } else {
            getRecentSEarchCodesAndFetch() // fetch
        }
    }

    private fun configureBackButton() = binding.back setOnDebounceClickListener {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun configureList() = binding.list.let { list ->
        list.adapter = SearchAdapter(onItemRemoved = {
            viewModel.onItemRemoved(it)
        }, onItemClicked = { position ->
            onItemClicked(position)
        }, viewModel.showRecently, viewLifecycleOwner)
        list.addItemDecoration(LinearItemDecoration(20))
    }

    private fun onItemClicked(position: Int) = lifecycleScope.launchWhenStarted {
        viewModel.onItemClicked(position)

        if (fromRecord) {
            recordViewModel.onLocationChanged()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        } else {
            // TODO
        }
    }

    private fun configureTextFieldSearch() = binding.textField.let { it ->
        it.setOnFocusChangeListener { _, hasFocus ->
            viewModel.focused.value = hasFocus
        }
    }

    private fun registerBackPressCallback() =
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainViewModel.changeMenu(HOME)
            }
        })

    private fun handleMainMenuChange() {
        mainViewModel.menu.observe(viewLifecycleOwner) {
            when (it) {
                SEARCH -> {
                    getRecentSEarchCodesAndFetch()
                }
                else -> {
                    viewModel.clear()
                }
            }
        }
    }

    private fun getRecentSEarchCodesAndFetch() = lifecycleScope.launchWhenStarted {
        viewModel.getRecentSearchCodesAndFetch()
    }

    companion object {
        fun newInstance(fromRecord: Boolean) = SearchFragment().apply {
            arguments = bundleOf("fromRecord" to fromRecord)
        }
    }
}