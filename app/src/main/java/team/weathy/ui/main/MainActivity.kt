package team.weathy.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import team.weathy.databinding.ActivityMainBinding
import team.weathy.ui.main.MainMenu.CALENDAR
import team.weathy.ui.main.MainMenu.HOME
import team.weathy.ui.main.MainMenu.SEARCH
import team.weathy.ui.main.calendar.CalendarFragment
import team.weathy.ui.main.calendar.HomeFragment
import team.weathy.ui.main.search.SearchFragment
import team.weathy.ui.nicknamechange.NicknameChangeActivity
import team.weathy.ui.setting.SettingActivity
import team.weathy.util.extensions.addFragment
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
    }

    private fun attachFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) return

        supportFragmentManager.commit {
            add(binding.fragmentContainer.id, HomeFragment::class.java, null, HomeFragment::class.java.simpleName)
        }
    }

    private fun configureToolbar() {
        binding.search setOnDebounceClickListener {
            navigateSearch()
        }
        binding.setting setOnDebounceClickListener {
            goToSetting()
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

    private fun navigateHome() = replaceFragment(binding.fragmentContainer, HomeFragment::class.java)

    private fun navigateCalendar() = replaceFragment(binding.fragmentContainer, CalendarFragment::class.java)

    private fun navigateSearch() = addFragment(binding.fragmentContainer, SearchFragment::class.java)

    override fun onBackPressed() {
        if (viewModel.menu.value == HOME) {
            super.onBackPressed()
        } else {
            viewModel.changeMenu(HOME)
        }
    }

    private fun goToSetting(){
        binding.setting.setOnDebounceClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}