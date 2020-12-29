package team.weathy.ui.main.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentCalendarBinding
import team.weathy.util.AutoClearedValue

class CalendarFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentCalendarBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentCalendarBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}