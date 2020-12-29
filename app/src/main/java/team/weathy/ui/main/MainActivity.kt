package team.weathy.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import team.weathy.databinding.ActivityMainBinding
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
            if (viewModel.menuIndex.value != 0) viewModel.changeMenu(0)
        }
        binding.calendar setOnDebounceClickListener {
            if (viewModel.menuIndex.value != 1) viewModel.changeMenu(1)
        }
    }

    private fun observeViewModel() {
        viewModel.run {
            menuIndex.observe(this@MainActivity) {
                when (it) {
                    0 -> navigateHome()
                    1 -> navigateCalendar()
                }
            }
        }
    }

    private fun navigateHome() {
        val exists = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)

        supportFragmentManager.commit {
            exists?.run {
                replace(binding.fragmentContainer.id, exists)
            } ?: replace(
                binding.fragmentContainer.id, HomeFragment::class.java, null, HomeFragment::class.java.simpleName
            )
        }
    }

    private fun navigateCalendar() {
        val exists = supportFragmentManager.findFragmentByTag(CalendarFragment::class.java.simpleName)

        supportFragmentManager.commit {
            exists?.run {
                replace(binding.fragmentContainer.id, exists)
            } ?: replace(
                binding.fragmentContainer.id,
                CalendarFragment::class.java,
                null,
                CalendarFragment::class.java.simpleName
            )
        }
    }

    override fun onBackPressed() {
        if (viewModel.menuIndex.value == 0) {
            super.onBackPressed()
        } else {
            viewModel.changeMenu(0)
        }
    }
}