package team.weathy.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {
    private val _selectedClothesTabIndex = MutableLiveData(0)
    val selectedClothesTabIndex: LiveData<Int> = _selectedClothesTabIndex

    val topClothes = MutableLiveData(listOf("상의1", "상의2"))
    val selectedTopClothes = MutableLiveData(listOf<String>())

    val bottomClothes = MutableLiveData(listOf("하의1", "하의2"))
    val selectedBottomClothes = MutableLiveData(listOf<String>())

    val outerClothes = MutableLiveData(listOf("아우터1", "아우터2"))
    val selectedOuterClothes = MutableLiveData(listOf<String>())

    val etcClothes = MutableLiveData(listOf("기타1", "기타2"))
    val selectedEtcClothes = MutableLiveData(listOf<String>())

    fun changeSelectedClothesTabIndex(tab: Int) {
        if (selectedClothesTabIndex.value != tab) {
            _selectedClothesTabIndex.value = tab
        }
    }
}