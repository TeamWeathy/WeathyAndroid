package team.weathy.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {
    private val _choicedClothesTabIndex = MutableLiveData(0)
    val choicedClothesTabIndex: LiveData<Int> = _choicedClothesTabIndex

    val clothesPairs = listOf<Pair<MutableLiveData<List<String>>, MutableLiveData<Set<Int>>>>(
        MutableLiveData(listOf("니트", "후드티", "티셔츠")) to MutableLiveData(setOf()),
        MutableLiveData(listOf("데님팬츠", "스커트", "슬랙스")) to MutableLiveData(setOf()),
        MutableLiveData(listOf("패딩", "코트", "점퍼")) to MutableLiveData(setOf()),
        MutableLiveData(listOf("목도리", "장갑", "모자")) to MutableLiveData(setOf()),
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

    fun onChipCheckedForDelete(index: Int) {
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

    fun addClothes(text: String) {
        val clothes = clothesPairs[choicedClothesTabIndex.value!!].first
        clothes.value = clothes.value!!.toMutableList().apply {
            add(0, text)
        }
    }

    fun deleteClothes(index : Int) {
        val clothes = clothesPairs[choicedClothesTabIndex.value!!].first
        clothes.value = clothes.value!!.toMutableList().apply {
            removeAt(index)
        }
    }

    fun allSelectedClothes() : Int {
        var selectedClothesCount = 0
        for (i in 0..3) {
            selectedClothesCount += clothesPairs[i].second.value!!.size
        }
        return selectedClothesCount
        }
    }