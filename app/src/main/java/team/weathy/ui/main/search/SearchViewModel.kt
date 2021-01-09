package team.weathy.ui.main.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import team.weathy.api.WeatherAPI
import team.weathy.di.ApiMock
import team.weathy.model.entity.OverviewWeather
import team.weathy.util.dateHourString
import team.weathy.util.extensions.MediatorLiveData
import team.weathy.util.extensions.addSources
import team.weathy.util.extensions.launchCatch
import java.time.LocalDateTime

class SearchViewModel @ViewModelInject constructor(@ApiMock private val weatherAPI: WeatherAPI) : ViewModel() {
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

    fun search() {
        launchCatch({
            weatherAPI.searchWeather(keyword = query.value!!, dateOrHourStr = LocalDateTime.now().dateHourString)
        }, loading, onSuccess = {
            if (it.list == null) {
                searchResult.value = listOf()
                showEmpty.value = true
            } else {
                searchResult.value = it.list
                showEmpty.value = false
            }

        })
    }

    fun onItemRemoved(position: Int) {
        searchResult.value = searchResult.value!!.toMutableList().apply {
            removeAt(position)
        }
    }
}

