package team.weathy.ui.record

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kotlinx.coroutines.FlowPreview
import team.weathy.api.ClothesAPI
import team.weathy.api.CreateClothesReq
import team.weathy.api.CreateWeathyReq
import team.weathy.api.DeleteClothesReq
import team.weathy.api.EditWeathyReq
import team.weathy.api.WeathyAPI
import team.weathy.di.Api
import team.weathy.model.entity.ClothCategory
import team.weathy.model.entity.ClothCategory.BOTTOM
import team.weathy.model.entity.ClothCategory.ETC
import team.weathy.model.entity.ClothCategory.OUTER
import team.weathy.model.entity.ClothCategory.TOP
import team.weathy.model.entity.OverviewWeather
import team.weathy.model.entity.WeatherStamp
import team.weathy.model.entity.WeathyCloth
import team.weathy.ui.record.RecordActivity.Companion.EXTRA_EDIT
import team.weathy.util.AppEvent
import team.weathy.util.EventLiveData
import team.weathy.util.SimpleEventLiveData
import team.weathy.util.UniqueIdentifier
import team.weathy.util.dateString
import team.weathy.util.emit
import team.weathy.util.extensions.MediatorLiveData
import team.weathy.util.extensions.addSources
import team.weathy.util.extensions.launchCatch
import team.weathy.util.extensions.updateSet
import team.weathy.util.location.LocationUtil
import team.weathy.util.monthDayFormat
import java.time.LocalDateTime

@FlowPreview
class RecordViewModel @ViewModelInject constructor(
    @Api private val clothesAPI: ClothesAPI,
    @Api private val weathyAPI: WeathyAPI,
    private val uniqueId: UniqueIdentifier,
    locationUtil: LocationUtil,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // region RECORD START
    val date = lastRecordNavigationTime
    val edit = savedStateHandle.get<Boolean>(EXTRA_EDIT) ?: false

    val weather = MutableLiveData<OverviewWeather>(locationUtil.selectedWeatherLocation.value)
    val weatherDate = date.toLocalDate().monthDayFormat
    val weatherRegion = weather.map {
        it ?: return@map ""
        it.region.name
    }
    val weatherIcon = weather.map {
        it ?: return@map null
        it.hourly.climate.weather.mediumIconId
    }
    val tempHigh = weather.map {
        it ?: return@map ""
        "${it.daily.temperature.maxTemp}°"
    }

    val tempLow = weather.map {
        it ?: return@map ""
        "${it.daily.temperature.minTemp}°"
    }

    fun onLocationChanged(weather: OverviewWeather) {
        this.weather.value = weather
    }

    // endregion

    // region CLOTHES SELECT/DELETE

    private val _choicedClothesTabIndex = MutableLiveData(0)
    val choicedClothesTabIndex: LiveData<Int> = _choicedClothesTabIndex

    private val selectedCategory
        get() = ClothCategory.fromId(_choicedClothesTabIndex.value!! + 1)

    val clothesTriple =
        listOf<Triple<MutableLiveData<List<WeathyCloth>>, MutableLiveData<Set<WeathyCloth>>, MutableLiveData<Set<WeathyCloth>>>>(
            Triple(MutableLiveData(listOf()), MutableLiveData(setOf()), MutableLiveData(setOf())),
            Triple(MutableLiveData(listOf()), MutableLiveData(setOf()), MutableLiveData(setOf())),
            Triple(MutableLiveData(listOf()), MutableLiveData(setOf()), MutableLiveData(setOf())),
            Triple(MutableLiveData(listOf()), MutableLiveData(setOf()), MutableLiveData(setOf())),
        )
    val clothes = MediatorLiveData<List<WeathyCloth>>().apply {
        value = clothesTriple[0].first.value!!
        clothesTriple.forEachIndexed { idx, (clohtes) ->
            addSource(clohtes) { list ->
                if (choicedClothesTabIndex.value == idx) value = list
            }
        }
        addSource(choicedClothesTabIndex) {
            value = clothesTriple[it].first.value!!
        }
    }
    val selectedClothes = MediatorLiveData<Set<WeathyCloth>>().apply {
        value = clothesTriple[0].second.value!!
        clothesTriple.forEachIndexed { idx, (_, selected) ->
            addSource(selected) { set ->
                if (choicedClothesTabIndex.value == idx) value = set
            }
        }
        addSource(choicedClothesTabIndex) {
            value = clothesTriple[it].second.value!!
        }
    }
    val selectedClothesForDelete = MediatorLiveData<Set<WeathyCloth>>().apply {
        value = clothesTriple[0].third.value!!
        clothesTriple.forEachIndexed { idx, (_, _, selectedForDelete) ->
            addSource(selectedForDelete) { set ->
                if (choicedClothesTabIndex.value == idx) value = set
            }
        }
        addSource(choicedClothesTabIndex) {
            value = clothesTriple[it].third.value!!
        }
    }

    val isButtonEnabled = MediatorLiveData(false) {
        addSources(
            clothesTriple[0].second, clothesTriple[1].second, clothesTriple[2].second, clothesTriple[3].second
        ) { c1, c2, c3, c4 ->
            value = !(c1.isEmpty() && c2.isEmpty() && c3.isEmpty() && c4.isEmpty())
        }
    }

    val onChipCheckedFailed = EventLiveData<WeathyCloth>()

    init {
        fetchClothes()
    }

    private fun fetchClothes() {
        launchCatch({
            clothesAPI.getClothes().closet
        }, onSuccess = {
            clothesTriple[0].first.value = it.top.clothes
            clothesTriple[1].first.value = it.bottom.clothes
            clothesTriple[2].first.value = it.outer.clothes
            clothesTriple[3].first.value = it.etc.clothes
        })
    }

    fun clearSelectedChipsForDelete() {
        clothesTriple.forEach {
            it.third.value = setOf()
        }
    }

    fun changeSelectedClothesTabIndex(tab: Int) {
        if (choicedClothesTabIndex.value != tab) {
            _choicedClothesTabIndex.value = tab
        }
    }

    fun onChipChecked(name: String) {
        val cloth = clothes.value!!.find { it.name == name } ?: return
        if (selectedClothes.value!!.contains(cloth)) return

        if (selectedClothes.value!!.size == 5) {
            onChipCheckedFailed.emit(cloth)
            return
        }
        clothesTriple[choicedClothesTabIndex.value!!].second.updateSet {
            add(cloth)
        }
    }

    fun onChipCheckedForDelete(name: String) = clothesTriple[choicedClothesTabIndex.value!!].third.updateSet {
        val cloth = clothes.value!!.find { it.name == name } ?: return
        add(cloth)
    }

    fun onChipUnchecked(name: String) = clothesTriple[choicedClothesTabIndex.value!!].second.updateSet {
        val cloth = clothes.value!!.find { it.name == name } ?: return
        remove(cloth)
    }

    fun onChipUncheckedForDelete(name: String) = clothesTriple[choicedClothesTabIndex.value!!].third.updateSet {
        val cloth = clothes.value!!.find { it.name == name } ?: return
        remove(cloth)
    }

    suspend fun addClothes(clothName: String) {
        val (clothes) = clothesTriple[choicedClothesTabIndex.value!!]

        val category = selectedCategory

        launchCatch({
            clothesAPI.createClothes(CreateClothesReq(category, clothName))
        }, onSuccess = {
            clothes.value = when (category) {
                TOP -> it.closet.top.clothes
                BOTTOM -> it.closet.bottom.clothes
                OUTER -> it.closet.outer.clothes
                ETC -> it.closet.etc.clothes
            }
        }).join()
    }

    suspend fun deleteClothes() {
        val deletedClothIds = clothesTriple.map { it.third.value!! }.flatten().map { it.id }

        launchCatch({
            clothesAPI.deleteClothes(
                DeleteClothesReq(deletedClothIds)
            ).closet
        }, onSuccess = {
            clothesTriple[0].first.value = it.top.clothes
            clothesTriple[1].first.value = it.bottom.clothes
            clothesTriple[2].first.value = it.outer.clothes
            clothesTriple[3].first.value = it.etc.clothes

            clothesTriple.forEach { (clothes, selected, _) ->
                selected.updateSet {
                    removeIf { it !in clothes.value!! }
                }
            }
        }).join()
    }

    fun allSelectedClothes(): Int {
        var selectedClothesCount = 0
        for (i in 0..3) {
            selectedClothesCount += clothesTriple[i].third.value!!.size
        }
        return selectedClothesCount
    }

    // endregion

    // region WEATHER RATING
    private val _selectedWeatherRating = MutableLiveData<WeatherStamp?>(null)
    val selectedWeatherRating: LiveData<WeatherStamp?> = _selectedWeatherRating

    fun changeSelectedWeatherRatingIndex(index: Int) {
        if (selectedWeatherRating.value?.index != index) {
            _selectedWeatherRating.value = WeatherStamp.fromIndex(index)
        }
    }


    // endregion

    // region WEATHER DETAIL

    val feedback = MutableLiveData("")
    val onRecordSuccess = SimpleEventLiveData()
    val onRecordEdited = SimpleEventLiveData()
    val onRecordFailed = SimpleEventLiveData()

    val feedbackFocused = MutableLiveData(false)
    val isSubmitButtonEnabled = feedback.map { it.isNotBlank() }

    fun submit(includeFeedback: Boolean) {
        val userId = uniqueId.userId ?: 0
        val date = this.date.toLocalDate().dateString
        val code = weather.value?.region?.code ?: 0L
        val clothes = clothesTriple.map { it.second.value!! }.flatten().map { it.id }
        val stampId = selectedWeatherRating.value?.id ?: 0

        val feedbackReq = if (includeFeedback) feedback.value!! else ""

        launchCatch({
            if (edit) {
                weathyAPI.editWeathy(lastEditWeathyId, EditWeathyReq(code, clothes, stampId, feedbackReq))
            } else {
                weathyAPI.createWeathy(
                    CreateWeathyReq(
                        userId, date, code, clothes, stampId, feedbackReq
                    )
                )
            }
        }, onSuccess = {
            AppEvent.onWeathyUpdated.emit()
            onRecordSuccess.emit()
        }, onFailure = {
            onRecordFailed.emit()
        })
    }

    // endregion

    companion object {
        var lastEditWeathyId: Int = -1
        var lastRecordNavigationTime: LocalDateTime = LocalDateTime.now()
    }
}