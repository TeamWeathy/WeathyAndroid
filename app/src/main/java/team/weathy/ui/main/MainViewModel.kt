package team.weathy.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _menuIndex: MutableLiveData<Int> = MutableLiveData(0)
    val menuIndex: LiveData<Int> = _menuIndex

    fun changeMenu(index: Int) {
        _menuIndex.value = index
    }

    private val _loadingCalendar = MutableLiveData(false)
    val loadingCalendar : LiveData<Boolean> = _loadingCalendar
}