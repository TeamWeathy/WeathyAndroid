package team.weathy.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import team.weathy.databinding.FragmentHomeBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener

class HomeFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentHomeBinding>()
    var cnt = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentHomeBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        binding.weathyQuestion.setOnDebounceClickListener {
//            if (cnt == 0) {
//                cnt++
//            } else {
//                cnt--
//            }
//        }
    }
}