package team.weathy.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import team.weathy.databinding.FragmentSearchBinding
import team.weathy.model.entity.OverviewWeather
import team.weathy.ui.main.MainMenu.HOME
import team.weathy.ui.main.MainMenu.SEARCH
import team.weathy.ui.main.MainViewModel
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.LinearItemDecoration
import team.weathy.util.extensions.hideKeyboard
import team.weathy.util.location.LocationUtil
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val fromRecord
        get() = arguments?.getBoolean("fromRecord") ?: false
    private var binding by AutoClearedValue<FragmentSearchBinding>()
    private val viewModel by viewModels<SearchViewModel>()

    @Inject
    lateinit var locationUtil: LocationUtil

    /**
     * This ViewModel is not initialized when fromRecord == true
     */
    private val mainViewModel by activityViewModels<MainViewModel>()

    /**
     * This ViewModel is not initialized when fromRecord == false or null
     */
    private val recordViewModel by activityViewModels<RecordViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            requireActivity().hideKeyboard()
            mainViewModel.changeMenu(HOME)
            isEnabled = false
        }
    }

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
            fetchRecentSearchLocations() // fetch
        }


        if (!fromRecord) {
            lifecycleScope.launchWhenStarted {
                locationUtil.selectedWeatherLocation.collect {
                    it ?: return@collect
                    binding.background.setImageResource(it.hourly.climate.weather.firstHomeBackgroundId)
                }
            }
        }
    }

    private fun configureBackButton() = binding.back setOnDebounceClickListener {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun configureList() = binding.list.let { list ->
        list.adapter = SearchAdapter(onItemRemoved = {
            viewModel.onItemRemoved(it)
        }, onItemClicked = { position, weather ->
            onItemClicked(position, weather)
        }, viewModel.showRecently, viewLifecycleOwner)
        list.addItemDecoration(LinearItemDecoration(20))
    }

    private fun onItemClicked(position: Int, weather: OverviewWeather) = lifecycleScope.launchWhenStarted {
        viewModel.onItemClicked(position)
        if (fromRecord) {
            recordViewModel.onLocationChanged(weather)
        } else {
            mainViewModel.onLocationChanged(weather)
        }
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun configureTextFieldSearch() = binding.textField.let { it ->
        it.setOnFocusChangeListener { _, hasFocus ->
            viewModel.focused.value = hasFocus
        }
    }

    private fun registerBackPressCallback() =
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

    override fun onResume() {
        super.onResume()
        onBackPressedCallback.isEnabled = true
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun handleMainMenuChange() {
        mainViewModel.menu.observe(viewLifecycleOwner) {
            when (it) {
                SEARCH -> {
                    fetchRecentSearchLocations()
                }
                else -> {
                    viewModel.clear()
                }
            }
        }
    }

    private fun fetchRecentSearchLocations() = lifecycleScope.launchWhenStarted {
        viewModel.getRecentSearchCodesAndFetch()
    }

    companion object {
        fun newInstance(fromRecord: Boolean) = SearchFragment().apply {
            arguments = bundleOf("fromRecord" to fromRecord)
        }
    }
}