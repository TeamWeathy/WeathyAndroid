package team.weathy.ui.record

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.FlowPreview
import team.weathy.api.ClothesAPI
import team.weathy.di.Api
import team.weathy.model.entity.OverviewWeather
import team.weathy.ui.record.RecordActivity.Companion.EXTRA_DATE
import team.weathy.ui.record.RecordActivity.Companion.EXTRA_EDIT
import team.weathy.util.EventLiveData
import team.weathy.util.extensions.updateList
import team.weathy.util.extensions.updateSet
import java.time.LocalDate
import java.time.LocalDateTime

@FlowPreview
class RecordViewModel @ViewModelInject constructor(
    @Api private val clothesAPI: ClothesAPI,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val date = savedStateHandle.get<LocalDate>(EXTRA_DATE) ?: LocalDate.now()
    val edit = savedStateHandle.get<Boolean>(EXTRA_EDIT) ?: false

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
    val selectedClothesForDelete = MediatorLiveData<Set<Int>>().apply {
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

    val onChipCheckedFailed = EventLiveData<Int>()


    //    val regionText = currentWeather.map { it?.region?.name ?: "" }
    //    val maxTempText = currentWeather.map { "${it?.daily?.temperature?.maxTemp ?: 0}°" }
    //    val minTempText = currentWeather.map { "${it?.daily?.temperature?.minTemp ?: 0}°" }

    fun changeSelectedClothesTabIndex(tab: Int) {
        if (choicedClothesTabIndex.value != tab) {
            _choicedClothesTabIndex.value = tab
        }
    }

    fun onChipChecked(index: Int) {
        if (selectedClothes.value!!.contains(index)) return

        if (selectedClothes.value!!.size == 5) {
            onChipCheckedFailed.emit(index + 1 /* because of add button */)
            return
        }
        clothesPairs[choicedClothesTabIndex.value!!].second.updateSet {
            add(index)
        }
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
            addAll(original.map { it + 1 })
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

    fun onLocationChanged(weather: OverviewWeather) {

    }

    companion object {
        var lastRecordNavigationTime: LocalDateTime = LocalDateTime.now()
    }
}