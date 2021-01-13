package team.weathy.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import team.weathy.databinding.ActivityMainBinding
import team.weathy.ui.main.MainMenu.*
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.ui.setting.SettingActivity
import team.weathy.util.AnimUtil
import team.weathy.util.dpFloat
import team.weathy.util.setOnDebounceClickListener
import java.time.LocalDateTime

@FlowPreview
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        configurePager()
        configureToolbar()
        configureBottomNavigation()
        observeViewModel()
    }

    private fun configurePager() = binding.fragmentPager.let { pager ->
        pager.adapter = MainFragmentAdapter(this)
        pager.isUserInputEnabled = false
    }

    private fun configureToolbar() {
        binding.search setOnDebounceClickListener {
            viewModel.changeMenu(SEARCH)
        }
        binding.setting setOnDebounceClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configureBottomNavigation() {
        binding.home setOnDebounceClickListener {
            viewModel.changeMenu(HOME)
        }
        binding.calendar setOnDebounceClickListener {
            viewModel.changeMenu(CALENDAR)
        }
        binding.fab setOnDebounceClickListener {
            navigateRecordAtToday()
        }
    }

    private fun observeViewModel() = viewModel.let { vm ->
        vm.menu.observe(this@MainActivity) { menu ->
            menu ?: return@observe
            when (menu) {
                HOME -> navigateHome()
                CALENDAR -> navigateCalendar()
                SEARCH -> navigateSearch()
            }
            adjustUiVisibilitiesWithMenu(menu)
        }
    }

    private fun adjustUiVisibilitiesWithMenu(menu: MainMenu) {
        adjustTopNavWithMenu(menu)
        adjustBottomNavWithMenu(menu)
    }

    private fun adjustTopNavWithMenu(menu: MainMenu) = when (menu) {
        HOME -> showTopNav()
        else -> hideTopNav()
    }

    private fun showTopNav() {
        val curTranslateY = binding.toolbar.translationY
        AnimUtil.runSpringAnimation(curTranslateY, 0f) {
            binding.toolbar.translationY = it
        }
    }

    private fun hideTopNav() {
        val curTranslateY = binding.toolbar.translationY
        AnimUtil.runSpringAnimation(curTranslateY, (-100).dpFloat) {
            binding.toolbar.translationY = it
        }
    }

    private fun adjustBottomNavWithMenu(menu: MainMenu) = when (menu) {
        SEARCH -> hideBottomNavSequentely()
        else -> showBottomNavSequently()
    }

    private fun showBottomNavSequently() = lifecycleScope.launchWhenStarted {
        val curTranslateY = binding.fab.translationY
        AnimUtil.runSpringAnimation(curTranslateY, 0f) {
            binding.fab.translationY = it
        }
        delay(50)
        AnimUtil.runSpringAnimation(curTranslateY, 0f) {
            binding.home.translationY = it
        }
        delay(50)
        AnimUtil.runSpringAnimation(curTranslateY, 0f) {
            binding.calendar.translationY = it
        }
    }

    private fun hideBottomNavSequentely() = lifecycleScope.launchWhenStarted {
        val curTranslateY = binding.fab.translationY
        AnimUtil.runSpringAnimation(curTranslateY, 82.dpFloat) {
            binding.fab.translationY = it
        }
        delay(50)
        AnimUtil.runSpringAnimation(curTranslateY, 82.dpFloat) {
            binding.home.translationY = it
        }
        delay(50)
        AnimUtil.runSpringAnimation(curTranslateY, 82.dpFloat) {
            binding.calendar.translationY = it
        }
    }

    private fun navigateHome() {
        binding.fragmentPager.setCurrentItem(0, false)
    }

    private fun navigateCalendar() {
        binding.fragmentPager.setCurrentItem(1, false)
    }

    private fun navigateSearch() {
        binding.fragmentPager.setCurrentItem(2, false)
    }

    private fun navigateRecordAtToday() {
        RecordViewModel.lastRecordNavigationTime = LocalDateTime.now()
        startActivity(RecordActivity.newIntent(this))
    }
}