package team.weathy.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _menu: MutableLiveData<MainMenu> = MutableLiveData(MainMenu.HOME)
    val menu: LiveData<MainMenu> = _menu

    private val _menuBeforeNavigateSearch = MutableLiveData(MainMenu.HOME)
    val menuBeforeNavigateSearch: LiveData<MainMenu> = _menuBeforeNavigateSearch

    fun changeMenu(menu: MainMenu) {
        if (_menu.value != menu) {
            _menuBeforeNavigateSearch.value = _menu.value
            _menu.value = menu
        }
    }

    private val _loadingCalendar = MutableLiveData(false)
    val loadingCalendar: LiveData<Boolean> = _loadingCalendar
}