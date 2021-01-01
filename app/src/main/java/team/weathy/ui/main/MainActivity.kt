package team.weathy.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import team.weathy.databinding.ActivityMainBinding
import team.weathy.ui.main.MainMenu.CALENDAR
import team.weathy.ui.main.MainMenu.HOME
import team.weathy.ui.main.MainMenu.SEARCH
import team.weathy.ui.main.calendar.CalendarFragment
import team.weathy.ui.main.calendar.HomeFragment
import team.weathy.util.setOnDebounceClickListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachFragments(savedInstanceState)
        configureBottomNavigation()
        observeViewModel()
    }

    private fun attachFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) return

        supportFragmentManager.commit {
            add(binding.fragmentContainer.id, HomeFragment::class.java, null, HomeFragment::class.java.simpleName)
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

    private fun observeViewModel() {
        viewModel.run {
            menu.observe(this@MainActivity) { menu ->
                menu ?: return@observe
                when (menu) {
                    HOME -> navigateHome()
                    CALENDAR -> navigateCalendar()
                    SEARCH -> navigateSearch()
                }
            }
        }
    }

    private fun navigateHome() = replaceFragment(HomeFragment::class.java)

    private fun navigateCalendar() = replaceFragment(CalendarFragment::class.java)

    private fun navigateSearch() {

    }

    private fun replaceFragment(clazz: Class<out Fragment>) {
        val tagName = clazz.simpleName
        val exists = supportFragmentManager.findFragmentByTag(tagName)

        supportFragmentManager.commit {
            exists?.run {
                replace(binding.fragmentContainer.id, exists)
            } ?: replace(
                binding.fragmentContainer.id, clazz, null, tagName
            )
        }
    }

    override fun onBackPressed() {
        if (viewModel.menu.value == HOME) {
            super.onBackPressed()
        } else {
            viewModel.changeMenu(HOME)
        }
    }
}