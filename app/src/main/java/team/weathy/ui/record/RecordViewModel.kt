package team.weathy.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {
    private val _choicedClothesTabIndex = MutableLiveData(0)
    val choicedClothesTabIndex: LiveData<Int> = _choicedClothesTabIndex

    val clothesPairs = listOf<Pair<MutableLiveData<List<String>>, MutableLiveData<Set<Int>>>>(
        MutableLiveData(listOf("상의1", "상의2")) to MutableLiveData(setOf()),
        MutableLiveData(listOf("하의1", "하의2")) to MutableLiveData(setOf()),
        MutableLiveData(listOf("아우터1", "아우터2")) to MutableLiveData(setOf()),
        MutableLiveData(listOf("기타1", "기타2")) to MutableLiveData(setOf()),
    )
    val clothes = MediatorLiveData<List<String>>().apply {
        value = clothesPairs[0].first.value!!
        addSource(choicedClothesTabIndex) {
            value = clothesPairs[it].first.value!!
        }
        clothesPairs.forEach {
            addSource(it.first) { list ->
                value = list
            }
        }
    }
    val selectedClothes = MediatorLiveData<Set<Int>>().apply {
        value = clothesPairs[0].second.value!!
        addSource(choicedClothesTabIndex) {
            value = clothesPairs[it].second.value!!
        }
        clothesPairs.forEach {
            addSource(it.second) { set ->
                value = set
            }
        }
    }


    fun changeSelectedClothesTabIndex(tab: Int) {
        if (choicedClothesTabIndex.value != tab) {
            _choicedClothesTabIndex.value = tab
        }
    }

    fun onChipChecked(index: Int) {
        val selectedClothes = clothesPairs[choicedClothesTabIndex.value!!].second
        selectedClothes.value = selectedClothes.value!!.toMutableSet().apply {
            add(index)
        }
    }

    fun onChipUnchecked(index: Int) {
        val selectedClothes = clothesPairs[choicedClothesTabIndex.value!!].second
        selectedClothes.value = selectedClothes.value!!.toMutableSet().apply {
            remove(index)
        }
    }
}