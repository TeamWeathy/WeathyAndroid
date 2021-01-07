package team.weathy.ui.record

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityRecordBinding
import team.weathy.ui.main.calendar.CalendarFragment
import team.weathy.ui.record.clothesdelete.RecordClothesDeleteFragment
import team.weathy.ui.record.clothesselect.RecordClothesSelectFragment
import team.weathy.ui.record.detail.RecordDetailFragment
import team.weathy.ui.record.locationchange.RecordLocationChangeFragment
import team.weathy.ui.record.start.RecordStartFragment
import team.weathy.ui.record.weatherrating.RecordWeatherRatingFragment
import team.weathy.util.extensions.addFragment
import team.weathy.util.extensions.popFragmentIfExist
import team.weathy.util.extensions.replaceFragment

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding

    private val viewModel by viewModels<RecordViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(binding.fragmentContainer, RecordStartFragment::class.java)
    }

    fun navigateStartToLocationChange() =
        addFragment(binding.fragmentContainer, RecordLocationChangeFragment::class.java)

    fun navigateStartToClothesSelect() = addFragment(binding.fragmentContainer, RecordClothesSelectFragment::class.java)

    fun navigateClothesSelectToClothesDelete() =
        addFragment(binding.fragmentContainer, RecordClothesDeleteFragment::class.java)

    fun navigateClothesSelectToWeatherRating() =
        addFragment(binding.fragmentContainer, RecordWeatherRatingFragment::class.java)

    fun navigateWeatherRatingToDetail() = addFragment(binding.fragmentContainer, RecordDetailFragment::class.java)

    fun replaceDetailToCalendar() = replaceFragment(binding.fragmentContainer, CalendarFragment::class.java)

    fun popClothesSelect() = popFragmentIfExist(RecordClothesSelectFragment::class.java)

    fun popClothesDelete() = popFragmentIfExist(RecordClothesDeleteFragment::class.java)

    fun popWeatherRating() = popFragmentIfExist(RecordWeatherRatingFragment::class.java)
}