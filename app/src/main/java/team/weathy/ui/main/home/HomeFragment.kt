package team.weathy.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.R
import team.weathy.databinding.FragmentHomeBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.PixelRatio
import team.weathy.util.dp
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentHomeBinding>()
    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var pixelRatio: PixelRatio

    private var shouldDisableThirdScene = false
    private var isHelpPopupShowing = false
    private var isFirstSceneShowing = true
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when {
                isHelpPopupShowing -> {
                    hideHelpPopup()
                }
                isFirstSceneShowing -> {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                else -> {
                    binding.container.transitionToState(R.layout.scene_home_first)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentHomeBinding.inflate(layoutInflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        weathyQuestionBtnClick()
        exitExplanationBtnClick()


        binding.downArrow.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha_repeat))

        binding.weatherImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim))

        binding.topBlur.pivotY = 0f

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

                        if (shouldDisableThirdScene) {
                            binding.container.definedTransitions.last().setEnable(false)
                        }
                    }
                    else -> {
                        isFirstSceneShowing = false
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })

        binding.weeklyCard.doOnLayout {
            binding.hourlyCard.doOnLayout {
                binding.detailCard.doOnLayout {
                    val screenHeight = pixelRatio.screenHeight
                    val marginTop = 78.dp
                    val marginBottom = 96.dp
                    val cardHeights = binding.weeklyCard.height + binding.hourlyCard.height + binding.detailCard.height
                    val marginBetweenCards = 24.dp * 2

                    shouldDisableThirdScene = marginTop + marginBottom + cardHeights + marginBetweenCards < screenHeight
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstSceneShowing = savedInstanceState?.getBoolean("isFirstSceneShowing") ?: true
        isHelpPopupShowing = savedInstanceState?.getBoolean("isHelpPopupShowing") ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFirstSceneShowing", isFirstSceneShowing)
        outState.putBoolean("isHelpPopupShowing", isHelpPopupShowing)
    }

    private fun weathyQuestionBtnClick() = binding.weathyQuestion.setOnDebounceClickListener {
        showHelpPopup()
    }

    private fun exitExplanationBtnClick() = binding.exitExplanation.setOnDebounceClickListener {
        hideHelpPopup()
    }

    private fun showHelpPopup() {
        TransitionManager.beginDelayedTransition(binding.container)
        binding.weathyExplanation.alpha = 1f
        isHelpPopupShowing = true
        binding.dim.alpha = 1f
        binding.dim.isClickable = true
        binding.dim.isFocusable = true
        binding.container.isInteractionEnabled = false
    }

    private fun hideHelpPopup() {
        TransitionManager.beginDelayedTransition(binding.container)
        binding.weathyExplanation.alpha = 0f
        isHelpPopupShowing = false
        binding.dim.alpha = 0f
        binding.dim.isClickable = false
        binding.dim.isFocusable = false
        binding.container.isInteractionEnabled = true
    }

}