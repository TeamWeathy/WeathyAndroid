package team.weathy.ui.landing

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import team.weathy.ui.landing.LandingAdapter.Item

class LandingViewModel @ViewModelInject constructor() : ViewModel() {
    private val _items = MutableLiveData(listOf(Item("1"), Item("2"), Item("3")))
    val items: LiveData<List<Item>> = _items

    private val _pagerIndex = MutableLiveData(0)
    val pagerIndex: LiveData<Int> = _pagerIndex

    fun onChangedPage(index: Int) {
        if (pagerIndex.value != index) _pagerIndex.value = index
    }
}