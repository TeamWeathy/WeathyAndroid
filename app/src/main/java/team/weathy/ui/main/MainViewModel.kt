package team.weathy.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import team.weathy.model.entity.OverviewWeather
import team.weathy.util.location.LocationUtil

class MainViewModel @ViewModelInject constructor(private val locationUtil: LocationUtil) : ViewModel() {
    private val _menu: MutableLiveData<MainMenu> = MutableLiveData(MainMenu.HOME)
    val menu: LiveData<MainMenu> = _menu

    fun changeMenu(menu: MainMenu) {
        if (_menu.value != menu) {
            _menu.value = menu
        }
    }

    fun onLocationChanged(weather: OverviewWeather) {
        locationUtil.selectOtherPlace(weather)
    }
}