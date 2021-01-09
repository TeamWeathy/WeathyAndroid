package team.weathy.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import team.weathy.R
import team.weathy.databinding.FragmentHomeBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener

class HomeFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentHomeBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentHomeBinding.inflate(layoutInflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.container.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                if (endId != R.layout.scene_home_third) binding.bg.crossfade = progress
            }

            override fun onTransitionCompleted(p0: MotionLayout?, curId: Int) {
                when (curId) {
                    R.layout.scene_home_first -> binding.weeklyWeatherView.resetAnimation()
                    R.layout.scene_home_second -> binding.weeklyWeatherView.startAnimation()
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })

        binding.downArrow setOnDebounceClickListener {
            binding.container.transitionToState(R.layout.scene_home_second)
        }
    }
}