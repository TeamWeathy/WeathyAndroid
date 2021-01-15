package team.weathy.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import team.weathy.R
import team.weathy.databinding.FragmentHomeBinding
import team.weathy.model.entity.Weather.BackgroundAnimation.RAIN
import team.weathy.model.entity.Weather.BackgroundAnimation.SNOW
import team.weathy.ui.main.MainActivity
import team.weathy.ui.main.MainMenu.HOME
import team.weathy.ui.main.MainViewModel
import team.weathy.ui.main.calendar.CalendarViewModel
import team.weathy.util.*
import java.time.LocalDate
import javax.inject.Inject


@FlowPreview
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentHomeBinding>()
    private val viewModel by viewModels<HomeViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val calendarViewModel by activityViewModels<CalendarViewModel>()

    @Inject
    lateinit var pixelRatio: PixelRatio

    @Inject
    lateinit var uniqueId: UniqueIdentifier

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            FragmentHomeBinding.inflate(layoutInflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        weathyQuestionBtnClick()
        exitExplanationBtnClick()

        if (!TestEnv.isInstrumentationTesting) {
            binding.downArrow.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha_repeat))
            binding.weatherImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim))
        }

        binding.topBlur.pivotY = 0f

        binding.container.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                if (endId != R.layout.scene_home_third) {
                    binding.bgFirst.alpha = 1 - progress
                    binding.bgSecond.alpha = progress
                }
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

        setNicknameText()

        viewModel.currentWeather.observe(viewLifecycleOwner) {
            it ?: return@observe
            val weather = it.hourly.climate.weather

            binding.rainFall.isVisible = weather.backgroundAnimation == RAIN
            binding.snowFall.isVisible = weather.backgroundAnimation == SNOW
        }

        mainViewModel.menu.observe(viewLifecycleOwner) {
            if (it != HOME) {
                hideHelpPopup()
            }
        }

        binding.recommended.root setOnDebounceClickListener {
            viewModel.recommendedWeathy.value?.dailyWeather?.date?.let { date ->
                AppEvent.onNavigateCurWeathyInCalendar.tryEmit(LocalDate.of(date.year, date.month, date.day))
            }
        }
    }

    private fun setNicknameText() {
        lifecycleScope.launchWhenStarted {
            uniqueId.userNickname.collect {
                binding.nickname.text = "${it}님이 기억하는"
            }
        }
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
        binding.container.isInteractionEnabled = false
        (activity as MainActivity).stateButton(false)
        binding.weathyQuestion.isEnabled = false
    }

    private fun hideHelpPopup() {
        TransitionManager.beginDelayedTransition(binding.container)
        binding.weathyExplanation.alpha = 0f
        isHelpPopupShowing = false
        binding.dim.alpha = 0f
        binding.container.isInteractionEnabled = true
        (activity as MainActivity).stateButton(true)
        binding.weathyQuestion.isEnabled = true
    }

}