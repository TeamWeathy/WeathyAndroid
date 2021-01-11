package team.weathy.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _menu: MutableLiveData<MainMenu> = MutableLiveData(MainMenu.HOME)
    val menu: LiveData<MainMenu> = _menu

    fun changeMenu(menu: MainMenu) {
        if (_menu.value != menu) {
            _menu.value = menu
        }
    }
}