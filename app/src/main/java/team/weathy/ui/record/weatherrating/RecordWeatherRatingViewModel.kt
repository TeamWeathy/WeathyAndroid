package team.weathy.ui.record.weatherrating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordWeatherRatingViewModel : ViewModel() {
	private val _selectedWeatherRatingIndex = MutableLiveData(-1)
	val selectedWeatherRatingIndex: LiveData<Int> = _selectedWeatherRatingIndex

	fun changeSelectedWeatherRatingIndex(tab: Int) {
		if (selectedWeatherRatingIndex.value != tab) {
			_selectedWeatherRatingIndex.value = tab
		}
	}
}