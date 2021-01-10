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
import team.weathy.di.ApiMock
import team.weathy.model.entity.OverviewWeather
import team.weathy.model.entity.RecentSearchCode
import team.weathy.util.dateHourString
import team.weathy.util.extensions.MediatorLiveData
import team.weathy.util.extensions.addSources
import team.weathy.util.extensions.launchCatch
import team.weathy.util.extensions.updateList
import java.time.LocalDateTime

@FlowPreview
class SearchViewModel @ViewModelInject constructor(
    @ApiMock private val weatherAPI: WeatherAPI, private val recentSearchCodeDao: RecentSearchCodeDao
) : ViewModel() {
    val focused = MutableLiveData(false)
    val query = MutableLiveData("")
    val loading = MutableLiveData(false)

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
        getRecentSearchCodesAndFetch()

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
            weatherAPI.searchWeather(keyword = query.value!!, dateOrHourStr = LocalDateTime.now().dateHourString)
        }, loading, onSuccess = { (list) ->
            searchResult.value = list ?: listOf()
        })
    }

    fun onItemRemoved(position: Int) {
        if (showRecently.value == true) {
            removeRecentSearchCode(position)
            recentlySearchResult.updateList {
                removeAt(position)
            }
        } else {
            searchResult.updateList {
                removeAt(position)
            }
        }
    }

    private fun getRecentSearchCodesAndFetch() = launchCatch({
        val codes = recentSearchCodeDao.getAll()
        fetchRecentSearchCodes(codes)
    }, loading, onSuccess = {
        recentlySearchResult.value = it
    })

    private suspend fun fetchRecentSearchCodes(codes: List<RecentSearchCode>) = codes.map {
        weatherAPI.fetchWeatherByLocation(
            code = it.code, dateOrHourStr = LocalDateTime.now().dateHourString
        ).weather!!
    }

    private fun removeRecentSearchCode(position: Int) = launchCatch({
        recentlySearchResult.value?.get(position)?.let {
            recentSearchCodeDao.delete(RecentSearchCode(it.daily.region.code))
        }
    })
}

