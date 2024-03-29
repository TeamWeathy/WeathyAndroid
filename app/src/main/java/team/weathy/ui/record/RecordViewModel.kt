package team.weathy.ui.record

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.google.gson.Gson
import kotlinx.coroutines.FlowPreview
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import team.weathy.api.*
import team.weathy.di.Api
import team.weathy.model.entity.ClothCategory
import team.weathy.model.entity.ClothCategory.BOTTOM
import team.weathy.model.entity.ClothCategory.ETC
import team.weathy.model.entity.ClothCategory.OUTER
import team.weathy.model.entity.ClothCategory.TOP
import team.weathy.model.entity.OverviewWeather
import team.weathy.model.entity.WeatherStamp
import team.weathy.model.entity.Weathy
import team.weathy.model.entity.WeathyCloth
import team.weathy.ui.record.RecordActivity.Companion.EXTRA_EDIT
import team.weathy.util.*
import team.weathy.util.extensions.MediatorLiveData
import team.weathy.util.extensions.addSources
import team.weathy.util.extensions.launchCatch
import team.weathy.util.extensions.updateSet
import team.weathy.util.location.LocationUtil
import java.time.LocalDateTime

@FlowPreview
class RecordViewModel @ViewModelInject constructor(
    @Api private val clothesAPI: ClothesAPI,
    @Api private val weathyAPI: WeathyAPI,
    @Api private val weatherAPI: WeatherAPI,
    private val uniqueId: UniqueIdentifier,
    locationUtil: LocationUtil,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // region RECORD START
    val date = lastRecordNavigationTime
    val edit = savedStateHandle.get<Boolean>(EXTRA_EDIT) ?: false
    val code = if (edit) lastEditWeathy?.region?.code else locationUtil.selectedWeatherLocation.value!!.region.code
    val weather = MutableLiveData<OverviewWeather?>(null)

    val weatherDate = date.toLocalDate().koFormat
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

    init {
        fetchWeather()
        fetchClothes()
    }

    private fun fetchWeather() {
        launchCatch({
            weatherAPI.fetchWeatherByLocation(code = code, dateOrHourStr = date.dateString)
        }, onSuccess = { res ->
            this.weather.value = res.body()!!.weather!!
        })
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

    var numClothes = mutableListOf<MutableLiveData<Int>>(MutableLiveData(0), MutableLiveData(0), MutableLiveData(0), MutableLiveData(0))

    val clothesTriple =
        listOf<Triple<MutableLiveData<List<WeathyCloth>>, MutableLiveData<Set<WeathyCloth>>, MutableLiveData<Set<WeathyCloth>>>>(
            Triple(MutableLiveData(listOf()), MutableLiveData(setOf()), MutableLiveData(setOf())),
            Triple(MutableLiveData(listOf()), MutableLiveData(setOf()), MutableLiveData(setOf())),
            Triple(MutableLiveData(listOf()), MutableLiveData(setOf()), MutableLiveData(setOf())),
            Triple(MutableLiveData(listOf()), MutableLiveData(setOf()), MutableLiveData(setOf())),
        )
    val clothes = MediatorLiveData<List<WeathyCloth>>().apply {
        value = clothesTriple[0].first.value!!
        clothesTriple.forEachIndexed { idx, (clothes) ->
            addSource(clothes) { list ->
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

    private fun fetchClothes() {
        launchCatch({
            if (edit) clothesAPI.getClothes(weathy_id = lastEditWeathy?.id).closet
            else clothesAPI.getClothes().closet
        }, onSuccess = {
            if (edit) {
                clothesTriple[0].second.value = lastEditWeathy?.closet?.top?.clothes?.toSet()
                clothesTriple[1].second.value = lastEditWeathy?.closet?.bottom?.clothes?.toSet()
                clothesTriple[2].second.value = lastEditWeathy?.closet?.outer?.clothes?.toSet()
                clothesTriple[3].second.value = lastEditWeathy?.closet?.etc?.clothes?.toSet()
            }
            numClothes[0].value = it.top.clothesNum
            numClothes[1].value = it.bottom.clothesNum
            numClothes[2].value = it.outer.clothesNum
            numClothes[3].value = it.etc.clothesNum

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

    fun changeChoicedClothesTabIndex(tab: Int) {
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

    suspend fun addClothes(clothName: String): Boolean {
        val (clothes) = clothesTriple[choicedClothesTabIndex.value!!]
        val category = selectedCategory
        var isSuccess = false

        launchCatch({
            if (edit) clothesAPI.createClothes(req = CreateClothesReq(category, clothName), weathy_id = lastEditWeathy?.id)
            else clothesAPI.createClothes(CreateClothesReq(category, clothName))
        }, onSuccess = {
            clothes.value = when (category) {
                TOP -> it.closet.top.clothes
                BOTTOM -> it.closet.bottom.clothes
                OUTER -> it.closet.outer.clothes
                ETC -> it.closet.etc.clothes
            }
            numClothes[0].value = it.closet.top.clothesNum
            numClothes[1].value = it.closet.bottom.clothesNum
            numClothes[2].value = it.closet.outer.clothesNum
            numClothes[3].value = it.closet.etc.clothesNum

            isSuccess = true
        }, onFailure = {
            isSuccess = false
        }).join()

        return isSuccess
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

            numClothes[0].value = it.top.clothesNum
            numClothes[1].value = it.bottom.clothesNum
            numClothes[2].value = it.outer.clothesNum
            numClothes[3].value = it.etc.clothesNum

            clothesTriple.forEach { (clothes, selected, _) ->
                selected.updateSet {
                    removeIf { it !in clothes.value!! }
                }
            }
        }).join()
    }

    fun countAllSelectedClothes(): Int {
        var selectedClothesCount = 0
        for (i in 0..3) {
            selectedClothesCount += clothesTriple[i].third.value!!.size
        }
        return selectedClothesCount
    }

    // endregion

    // region WEATHER RATING
    private val _selectedWeatherRating = MutableLiveData<WeatherStamp?>(lastEditWeathy?.stampId)
    val selectedWeatherRating: LiveData<WeatherStamp?> = _selectedWeatherRating

    fun changeSelectedWeatherRatingIndex(index: Int) {
        if (selectedWeatherRating.value?.index != index) {
            _selectedWeatherRating.value = WeatherStamp.fromIndex(index)
        }
    }
    // endregion

    // region WEATHER DETAIL
    val feedback = MutableLiveData(if (edit) lastEditWeathy?.feedback else null)
    val img = MutableLiveData<RequestBody>(null)
    var imgFromEdit = if (edit) lastEditWeathy?.imgUrl else null
    val isDelete = MutableLiveData(false)
    val onRecordSuccess = SimpleEventLiveData()
    val onRecordEdited = SimpleEventLiveData()
    val onRecordFailed = SimpleEventLiveData()

    val feedbackFocused = MutableLiveData(false)
    val isSubmitButtonEnabled = feedback.map { it?.isNotBlank() ?: false }

    fun submit(includeFeedback: Boolean) {
        val userId = uniqueId.userId ?: 0
        val date = this.date.toLocalDate().dateString
        val code = weather.value?.region?.code ?: 0L
        val clothes = clothesTriple.map { it.second.value!! }.flatten().map { it.id }
        // [2,18]
        val stampId = selectedWeatherRating.value?.id ?: 0

        val feedbackReq = if (includeFeedback) {
            if (feedback.value != null) feedback.value!! else null
        } else null

        launchCatch({
            val gson = Gson()

            if (edit) {
                val editWeathyReq = EditWeathyReq(code, clothes, stampId, feedbackReq, isDelete.value!!)
                val jsonString = gson.toJson(editWeathyReq)
                weathyAPI.editWeathy(lastEditWeathy?.id ?: 0,
                    weathy = RequestBody.create(MediaType.parse("text/plain"), jsonString),
                    img = if(img.value != null) MultipartBody.Part.createFormData("img", "tmp.jpg", img.value!!) else null
                )
            } else {
                val createWeathyReq = CreateWeathyReq(userId, date, code, clothes, stampId, feedbackReq)
                val jsonString = gson.toJson(createWeathyReq)
                weathyAPI.createWeathy(
                    weathy = RequestBody.create(MediaType.parse("text/plain"), jsonString),
                    img = if(img.value != null) MultipartBody.Part.createFormData("img", "tmp.jpg", img.value!!) else null
                )
            }
        }, onSuccess = {
            AppEvent.onWeathyUpdated.emit()
            AppEvent.onNavigateCurWeathyInCalendar.tryEmit(this.date.toLocalDate())
            if (edit) onRecordEdited.emit()
            else onRecordSuccess.emit()
        }, onFailure = {
            onRecordFailed.emit()
        })
    }

    override fun onCleared() {
        super.onCleared()

        lastEditWeathy = null
    }

    // endregion

    companion object {
        var lastEditWeathy: Weathy? = null
        var lastRecordNavigationTime: LocalDateTime = LocalDateTime.now()
    }
}