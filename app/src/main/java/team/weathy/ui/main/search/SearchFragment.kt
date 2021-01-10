package team.weathy.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.databinding.FragmentSearchBinding
import team.weathy.ui.main.MainMenu
import team.weathy.ui.main.MainMenu.HOME
import team.weathy.ui.main.MainViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.LinearItemDecoration
import team.weathy.util.debugE
import team.weathy.util.setOnDebounceClickListener

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentSearchBinding>()
    private val viewModel by viewModels<SearchViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSearchBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configureBackButton()
        configureList()
        configureTextFieldSearch()

        handleBackPress()
        handleMainMenuChange()
    }

    private fun configureBackButton() = binding.back setOnDebounceClickListener {
        requireActivity().onBackPressed()
    }

    @OptIn(FlowPreview::class)
    private fun configureList() = binding.list.let { list ->
        list.adapter = SearchAdapter(onItemRemoved = {
            viewModel.onItemRemoved(it)
        }, viewModel.showRecently, viewLifecycleOwner)
        list.addItemDecoration(LinearItemDecoration(20))
    }

    private fun configureTextFieldSearch() = binding.textField.let { it ->
        it.setOnFocusChangeListener { _, hasFocus ->
            viewModel.focused.value = hasFocus
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        debugE("destory")
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainViewModel.changeMenu(HOME)
            }
        })
    }

    private fun handleMainMenuChange() {
        mainViewModel.menu.observe(viewLifecycleOwner) {
            if (it != MainMenu.SEARCH) viewModel.clear()
        }
    }
}