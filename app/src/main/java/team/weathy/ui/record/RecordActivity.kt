package team.weathy.ui.record

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.databinding.ActivityRecordBinding
import team.weathy.ui.main.calendar.CalendarFragment
import team.weathy.ui.main.search.SearchFragment
import team.weathy.ui.record.clothesdelete.RecordClothesDeleteFragment
import team.weathy.ui.record.clothesselect.RecordClothesSelectFragment
import team.weathy.ui.record.detail.RecordDetailFragment
import team.weathy.ui.record.start.RecordStartFragment
import team.weathy.ui.record.weatherrating.RecordWeatherRatingFragment
import team.weathy.util.extensions.popFragmentIfExist
import team.weathy.util.extensions.replaceFragment

@FlowPreview
@AndroidEntryPoint
class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding

    private val viewModel by viewModels<RecordViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(binding.fragmentContainer, RecordStartFragment::class.java, withAnim = false)
    }

    fun navigateStartToLocationChange() =
        replaceFragment(binding.fragmentContainer, SearchFragment.newInstance(true), true)

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

    companion object {
        const val EXTRA_EDIT = "edit"

        fun newIntent(context: Context, edit: Boolean = false): Intent {
            return Intent(context, RecordActivity::class.java).apply {
                putExtra(EXTRA_EDIT, edit)
            }
        }
    }
}