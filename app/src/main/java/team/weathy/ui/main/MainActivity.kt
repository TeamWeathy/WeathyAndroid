package team.weathy.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import team.weathy.databinding.ActivityMainBinding
import team.weathy.ui.main.MainMenu.*
import team.weathy.ui.main.calendar.CalendarFragment
import team.weathy.ui.main.calendar.HomeFragment
import team.weathy.ui.main.search.SearchFragment
import team.weathy.ui.setting.SettingActivity
import team.weathy.util.AnimUtil
import team.weathy.util.StatusBarUtil
import team.weathy.util.dpFloat
import team.weathy.util.extensions.addFragment
import team.weathy.util.extensions.popFragmentIfExist
import team.weathy.util.extensions.replaceFragment
import team.weathy.util.setOnDebounceClickListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachFragments(savedInstanceState)
        configureToolbar()
        configureBottomNavigation()
        observeViewModel()

        StatusBarUtil.collapseStatusBar(this)
    }

    private fun attachFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) return

        supportFragmentManager.commit {
            add(binding.fragmentContainer.id, HomeFragment::class.java, null, HomeFragment::class.java.simpleName)
        }
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
    }

    private fun observeViewModel() = viewModel.let { vm ->
        vm.menu.observe(this@MainActivity) { menu ->
            menu ?: return@observe
            when (menu) {
                HOME -> navigateHome()
                CALENDAR -> navigateCalendar()
                SEARCH -> navigateSearch()
            }
            adjustBottomNavigationBarWithMenu(menu)
        }
    }

    private fun adjustBottomNavigationBarWithMenu(menu: MainMenu) {
        val curTranslateY = binding.fab.translationY
        if (menu == SEARCH) {
            hideBottomNavSequentely(curTranslateY)
        } else {
            showBottomNavSequently(curTranslateY)
        }
    }

    private fun hideBottomNavSequentely(startMargin: Float) = lifecycleScope.launchWhenStarted {
        AnimUtil.runSpringAnimation(startMargin, 100.dpFloat, 100f) {
            binding.fab.translationY = it
        }
        delay(100)
        AnimUtil.runSpringAnimation(startMargin, 100.dpFloat, 100f) {
            binding.home.translationY = it
        }
        delay(100)
        AnimUtil.runSpringAnimation(startMargin, 100.dpFloat, 100f) {
            binding.calendar.translationY = it
        }
    }

    private fun showBottomNavSequently(startMargin: Float) = lifecycleScope.launchWhenStarted {
        AnimUtil.runSpringAnimation(startMargin, 0f, 100f) {
            binding.fab.translationY = it
        }
        delay(100)
        AnimUtil.runSpringAnimation(startMargin, 0f, 100f) {
            binding.home.translationY = it
        }
        delay(100)
        AnimUtil.runSpringAnimation(startMargin, 0f, 100f) {
            binding.calendar.translationY = it
        }
    }

    private fun navigateHome() {
        popFragmentIfExist(SearchFragment::class.java)
        replaceFragment(binding.fragmentContainer, HomeFragment::class.java)
    }

    private fun navigateCalendar() {
        popFragmentIfExist(SearchFragment::class.java)
        replaceFragment(binding.fragmentContainer, CalendarFragment::class.java)
    }

    private fun navigateSearch() = addFragment(binding.fragmentContainer, SearchFragment::class.java)

    override fun onBackPressed() {
        when (viewModel.menu.value) {
            HOME -> super.onBackPressed()
            CALENDAR -> viewModel.changeMenu(HOME)
            else -> viewModel.changeMenu(viewModel.menuBeforeNavigateSearch.value!!)
        }
    }
}