package team.weathy.ui.record

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import team.weathy.R
import team.weathy.databinding.ActivityRecordBinding
import team.weathy.ui.main.calendar.CalendarFragment
import team.weathy.ui.record.clothesdelete.RecordClothesDeleteFragment
import team.weathy.ui.record.clothesselect.RecordClothesSelectFragment
import team.weathy.ui.record.detail.RecordDetailFragment
import team.weathy.ui.record.locationchange.RecordLocationChangeFragment
import team.weathy.ui.record.start.RecordStartFragment
import team.weathy.ui.record.weatherrating.RecordWeatherRatingFragment
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
        replaceFragment(binding.fragmentContainer, RecordLocationChangeFragment::class.java, true)

    fun navigateStartToClothesSelect() =
        replaceFragment(binding.fragmentContainer, RecordClothesSelectFragment::class.java, true)

    fun navigateClothesSelectToClothesDelete() =
        replaceFragment(binding.fragmentContainer, RecordClothesDeleteFragment::class.java, true)

    fun navigateClothesSelectToWeatherRating() =
        replaceFragment(binding.fragmentContainer, RecordWeatherRatingFragment::class.java, true)

    fun navigateWeatherRatingToDetail() =
        replaceFragment(binding.fragmentContainer, RecordDetailFragment::class.java, true)

    fun replaceDetailToCalendar() = replaceFragment(binding.fragmentContainer, CalendarFragment::class.java, true)

    fun popClothesSelect() = popFragmentIfExist(RecordClothesSelectFragment::class.java)

    fun popClothesDelete() = popFragmentIfExist(RecordClothesDeleteFragment::class.java)

    fun popWeatherRating() = popFragmentIfExist(RecordWeatherRatingFragment::class.java)
}