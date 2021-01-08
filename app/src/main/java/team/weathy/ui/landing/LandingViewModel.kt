package team.weathy.ui.landing

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LandingViewModel @ViewModelInject constructor() : ViewModel() {
    private val _pagerIndex = MutableLiveData(0)
    val pagerIndex: LiveData<Int> = _pagerIndex

    fun onChangedPage(index: Int) {
        if (pagerIndex.value != index) _pagerIndex.value = index
    }
}