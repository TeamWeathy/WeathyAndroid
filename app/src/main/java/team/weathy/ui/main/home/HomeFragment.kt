package team.weathy.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.R
import team.weathy.databinding.FragmentHomeBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentHomeBinding>()
    private val viewModel by viewModels<HomeViewModel>()

    private var isFirstSceneShowing = true
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFirstSceneShowing) {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                binding.container.transitionToState(R.layout.scene_home_first)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentHomeBinding.inflate(layoutInflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.container.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                if (endId != R.layout.scene_home_third) binding.bg.crossfade = progress
            }

            override fun onTransitionCompleted(p0: MotionLayout?, curId: Int) {
                when (curId) {
                    R.layout.scene_home_first -> {
                        isFirstSceneShowing = true
                        binding.hourlyView.resetAnimation()
                        binding.weeklyView.resetAnimation()
                    }
                    R.layout.scene_home_second -> {
                        isFirstSceneShowing = false
                        binding.hourlyView.startAnimation()
                        binding.weeklyView.startAnimation()
                    }
                    else -> {
                        isFirstSceneShowing = false
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })

        binding.downArrow setOnDebounceClickListener {
            binding.container.transitionToState(R.layout.scene_home_second)
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstSceneShowing = savedInstanceState?.getBoolean("isFirstSceneShowing") ?: true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFirstSceneShowing", isFirstSceneShowing)
    }

}