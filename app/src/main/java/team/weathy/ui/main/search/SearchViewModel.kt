package team.weathy.ui.main.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    val query = MutableLiveData("")
}