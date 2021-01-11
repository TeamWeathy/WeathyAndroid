package team.weathy.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import team.weathy.util.debugE
import team.weathy.util.extensions.updateList
import team.weathy.util.extensions.updateSet

class RecordViewModel : ViewModel() {
    private val _choicedClothesTabIndex = MutableLiveData(0)
    val choicedClothesTabIndex: LiveData<Int> = _choicedClothesTabIndex

    val clothesPairs = listOf<Pair<MutableLiveData<List<String>>, MutableLiveData<Set<Int>>>>(
        MutableLiveData(
            listOf(
                "니트", "후드티", "티셔츠", "하이",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",
                "더미asdasdasdasdasdasdasd",


                )
        ) to MutableLiveData(setOf()),
        MutableLiveData(listOf("데님팬츠", "스커트", "슬랙스")) to MutableLiveData(setOf()),
        MutableLiveData(listOf("패딩", "코트", "점퍼")) to MutableLiveData(setOf()),
        MutableLiveData(listOf("목도리", "장갑", "모자")) to MutableLiveData(setOf()),
    )
    val clothes = MediatorLiveData<List<String>>().apply {
        value = clothesPairs[0].first.value!!
        clothesPairs.forEach {
            addSource(it.first) { list ->
                value = list
            }
        }
        addSource(choicedClothesTabIndex) {
            value = clothesPairs[it].first.value!!
        }
    }
    val selectedClothes = MediatorLiveData<Set<Int>>().apply {
        value = clothesPairs[0].second.value!!
        clothesPairs.forEach {
            addSource(it.second) { set ->
                value = set
            }
        }
        addSource(choicedClothesTabIndex) {
            value = clothesPairs[it].second.value!!
        }
    }

    fun changeSelectedClothesTabIndex(tab: Int) {
        if (choicedClothesTabIndex.value != tab) {
            _choicedClothesTabIndex.value = tab
        }
    }

    fun onChipChecked(index: Int) = clothesPairs[choicedClothesTabIndex.value!!].second.updateSet {
        add(index)
    }

    fun onChipCheckedForDelete(index: Int) = clothesPairs[choicedClothesTabIndex.value!!].second.updateSet {
        add(index)
    }

    fun onChipUnchecked(index: Int) = clothesPairs[choicedClothesTabIndex.value!!].second.updateSet {
        remove(index)
    }

    fun addClothes(text: String) {
        val (clothes, selectedIndices) = clothesPairs[choicedClothesTabIndex.value!!]

        clothes.updateList {
            add(0, text)
        }

        selectedIndices.updateSet {
            val original = toMutableSet()
            clear()
            original.map { it + 1 }.forEach(this::add)
        }
    }

    fun deleteClothes(index: Int) = clothesPairs[choicedClothesTabIndex.value!!].first.updateList {
        removeAt(index)
    }

    fun allSelectedClothes(): Int {
        var selectedClothesCount = 0
        for (i in 0..3) {
            selectedClothesCount += clothesPairs[i].second.value!!.size
        }
        return selectedClothesCount
    }
}