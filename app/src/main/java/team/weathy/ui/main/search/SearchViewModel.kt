package team.weathy.ui.main.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import team.weathy.api.WeatherAPI
import team.weathy.database.RecentSearchCodeDao
import team.weathy.di.ApiMock
import team.weathy.model.entity.OverviewWeather
import team.weathy.model.entity.RecentSearchCode
import team.weathy.util.dateHourString
import team.weathy.util.debugE
import team.weathy.util.extensions.MediatorLiveData
import team.weathy.util.extensions.addSources
import team.weathy.util.extensions.launchCatch
import java.time.LocalDateTime

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
            showRecently, recentlySearchResult, searchResult
        ) { showRecently, recentlySearchResult, searchResult ->
            value = if (showRecently) recentlySearchResult.isEmpty() else searchResult.isEmpty()
        }
    }

    init {
        getRecentSearchCodesAndFetch()
    }

    fun search() {
        launchCatch({
            weatherAPI.searchWeather(keyword = query.value!!, dateOrHourStr = LocalDateTime.now().dateHourString)
        }, loading, onSuccess = { (list) ->
            searchResult.value = list ?: listOf()
            showEmpty.value = list == null
        })
    }

    fun onItemRemoved(position: Int) {
        if (showRecently.value == true) {
            removeRecentSearchCode(position)
            recentlySearchResult.removeAtPosition(position)

        } else {
            searchResult.removeAtPosition(position)
        }
    }


    private fun getRecentSearchCodesAndFetch() = launchCatch({
        val codes = recentSearchCodeDao.getAll()
        debugE(codes)
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
            debugE(it)
            recentSearchCodeDao.delete(RecentSearchCode(it.daily.region.code))
        }
    })

    private fun MutableLiveData<List<OverviewWeather>>.removeAtPosition(position: Int) {
        value = value?.toMutableList()?.apply {
            removeAt(position)
        }
    }
}

