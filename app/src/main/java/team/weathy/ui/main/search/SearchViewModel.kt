package team.weathy.ui.main.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import team.weathy.api.WeatherAPI
import team.weathy.database.RecentSearchCodeDao
import team.weathy.di.Api
import team.weathy.model.entity.OverviewWeather
import team.weathy.model.entity.RecentSearchCode
import team.weathy.util.dateHourString
import team.weathy.util.dateString
import team.weathy.util.debugE
import team.weathy.util.extensions.MediatorLiveData
import team.weathy.util.extensions.addSources
import team.weathy.util.extensions.launchCatch
import team.weathy.util.extensions.updateList
import java.time.LocalDateTime

@FlowPreview
class SearchViewModel @ViewModelInject constructor(
    @Api private val weatherAPI: WeatherAPI,
    private val recentSearchCodeDao: RecentSearchCodeDao,
) : ViewModel() {
    val dateHourString = MutableLiveData(LocalDateTime.now().dateHourString)
    val dateString = MutableLiveData(LocalDateTime.now().dateString)
    val focused = MutableLiveData(false)
    val query = MutableLiveData("")
    val loading = MutableLiveData(false)
    val fromRecord = MutableLiveData(false)

    val recentlySearchResult = MutableLiveData<List<OverviewWeather>>(listOf())
    val searchResult = MutableLiveData<List<OverviewWeather>>(listOf())

    val showRecently = MediatorLiveData(true) {
        addSources(focused, query) { focused, query ->
            value = !focused && query.isEmpty()
        }
    }
    val showEmpty = MediatorLiveData(true) {
        addSources(
            showRecently, recentlySearchResult, searchResult, query, loading,
        ) { showRecently, recentlySearchResult, searchResult, query, loading ->
            value = when {
                loading -> false
                else -> if (showRecently) recentlySearchResult.isEmpty() else (searchResult.isEmpty() && query.isNotBlank())
            }
        }
    }

    init {
        viewModelScope.launch {
            query.asFlow().drop(1).distinctUntilChanged().debounce(200).filter(String::isNotEmpty).collect {
                search()
            }
        }
    }

    fun clear() {
        focused.value = false
        query.value = ""
        loading.value = false
        recentlySearchResult.value = listOf()
        searchResult.value = listOf()
    }

    private fun search() {
        launchCatch({
            if (fromRecord.value!!) weatherAPI.searchWeather(keyword = query.value!!, dateOrHourStr = dateString.value!!)
            else weatherAPI.searchWeather(keyword = query.value!!, dateOrHourStr = dateHourString.value!!)
        }, loading, onSuccess = { (list) ->
            searchResult.value = list ?: listOf()
        })
    }

    suspend fun onItemClicked(position: Int) {
        kotlin.runCatching {
            searchResult.value?.get(position)?.region?.code?.let {
                recentSearchCodeDao.add(RecentSearchCode(it))
                getRecentSearchCodesAndFetch()
            }
        }
    }

    suspend fun getRecentSearchCodesAndFetch() {
        loading.value = true

        kotlin.runCatching {
            val codes = recentSearchCodeDao.getAll()
            fetchRecentSearchCodes(codes)
        }.onSuccess {
            recentlySearchResult.value = it
        }.onFailure {
            debugE(it)
        }

        loading.value = false
    }

    private suspend fun fetchRecentSearchCodes(codes: List<RecentSearchCode>) = codes.mapNotNull {
        if (fromRecord.value!!) {
            weatherAPI.fetchWeatherByLocation(
                code = it.code, dateOrHourStr = dateString.value!!
            ).body()?.weather
        } else {
            weatherAPI.fetchWeatherByLocation(
                code = it.code, dateOrHourStr = dateHourString.value!!
            ).body()?.weather
        }
    }

    fun onItemRemoved(position: Int) {
        if (showRecently.value == false) return
        val code = recentlySearchResult.value?.get(position)?.region?.code ?: return

        removeRecentSearchCode(code)
        recentlySearchResult.updateList {
            removeAt(position)
        }
    }

    private fun removeRecentSearchCode(code: Long) = launchCatch({
        recentSearchCodeDao.delete(code)
    })
}

