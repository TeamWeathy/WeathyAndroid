package team.weathy.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import team.weathy.R
import team.weathy.databinding.ActivityMainBinding
import team.weathy.ui.main.MainMenu.*
import team.weathy.ui.main.calendar.CalendarFragment
import team.weathy.ui.main.calendar.CalendarViewModel
import team.weathy.ui.main.home.HomeFragment
import team.weathy.ui.main.search.SearchFragment
import team.weathy.ui.record.RecordActivity
import team.weathy.ui.record.RecordViewModel
import team.weathy.ui.setting.SettingActivity
import team.weathy.util.*
import team.weathy.util.extensions.showToast
import java.time.LocalDate
import java.time.LocalDateTime

@FlowPreview
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()
    private val calendarViewModel by viewModels<CalendarViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        checkLocationPermission()

//        configurePager()
        configureToolbar()
        configureBottomNavigation()
        observeViewModel()

        lifecycleScope.launchWhenStarted {
            AppEvent.onNavigateCurWeathyInCalendar.collect { date ->
                viewModel.changeMenu(CALENDAR)
                calendarViewModel.onCurDateChanged(date)
                calendarViewModel.onSelectedDateChanged(date)
            }
        }
    }

    private fun checkLocationPermission() {
        PermissionUtil.requestLocationPermissions(this, object : PermissionUtil.PermissionListener {
            override fun onAnyPermissionsPermanentlyDeined(
                    deniedPermissions: List<String>, permanentDeniedPermissions: List<String>
            ) {
                showToast("위치 권한이 영구적으로 거부되었습니다")
            }
        })
    }

//    private fun configurePager() = binding.fragmentPager.let { pager ->
//        pager.adapter = MainFragmentAdapter(this)
//        pager.isUserInputEnabled = false
//    }

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
            if (calendarViewModel.todayWeathy.value != null
                    && LocalDate.of(calendarViewModel.todayWeathy.value!!.dailyWeather.date.year,
                            calendarViewModel.todayWeathy.value!!.dailyWeather.date.month,
                            calendarViewModel.todayWeathy.value!!.dailyWeather.date.day) == LocalDate.now()) {
                showToast("웨디는 하루에 하나만 기록할 수 있어요.")
            } else {
                calendarViewModel.todayWeathy.value = null
                navigateRecordAtToday()
            }
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
        changeFragment(HomeFragment())
    }

    private fun navigateCalendar() {
        changeFragment(CalendarFragment())
    }

    private fun navigateSearch() {
        changeFragment(SearchFragment())
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun navigateRecordAtToday() {
        RecordViewModel.lastRecordNavigationTime = LocalDateTime.now()
        startActivity(RecordActivity.newIntent(this))
    }

    fun stateButton(state: Boolean) {
        binding.search.isEnabled = state
        binding.setting.isEnabled = state
        binding.home.isEnabled = state
        binding.calendar.isEnabled = state
        binding.fab.isEnabled = state
    }

    fun onDim(){
        binding.bottomNav.setBackgroundResource(R.color.transparent)
    }

    fun offDim(){
        binding.bottomNav.setBackgroundResource(R.drawable.main_box_bottomblur)
    }
}