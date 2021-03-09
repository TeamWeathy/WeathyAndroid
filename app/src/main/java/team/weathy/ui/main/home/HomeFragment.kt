package team.weathy.ui.main.home

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
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
import androidx.lifecycle.observe
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
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
import team.weathy.util.*
import team.weathy.util.location.LocationUtil
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.abs


@FlowPreview
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentHomeBinding>()
    private val viewModel by viewModels<HomeViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var pixelRatio: PixelRatio

    @Inject
    lateinit var uniqueId: UniqueIdentifier

    @Inject
    lateinit var locationUtil: LocationUtil

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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) =
            FragmentHomeBinding.inflate(layoutInflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        weathyQuestionBtnClick()
        exitExplanationBtnClick()

        if (!TestEnv.isInstrumentationTesting) {
            binding.downArrow.startAnimation(
                    AnimationUtils.loadAnimation(
                            context,
                            R.anim.alpha_repeat
                    )
            )
            binding.weatherImage.doOnLayout {
                ObjectAnimator.ofFloat(
                        binding.weatherImage,
                        "translationY",
                        -0.02f * it.height,
                        0.02f * it.height
                )
                        .apply {
                            duration = 1000L
                            repeatMode = ObjectAnimator.REVERSE
                            repeatCount = ObjectAnimator.INFINITE
                            setAutoCancel(true)
                            start()
                        }
            }
        }

        binding.topBlur.pivotY = 0f

        binding.container.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {
            }

            override fun onTransitionChange(
                    p0: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
            ) {

            }

            override fun onTransitionCompleted(p0: MotionLayout?, curId: Int) {
                when (curId) {
                    R.layout.scene_home_first -> {
                        isFirstSceneShowing = true
                        resetCardAnimations()
                    }
                    R.layout.scene_home_second -> {
                        isFirstSceneShowing = false
                        startCardAnimations()

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
                    val cardHeights =
                            binding.weeklyCard.height + binding.hourlyCard.height + binding.detailCard.height
                    val marginBetweenCards = 24.dp * 2

                    shouldDisableThirdScene =
                            marginTop + marginBottom + cardHeights + marginBetweenCards < screenHeight
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

        viewModel.recommendedWeathy.observe(viewLifecycleOwner) {
            it?.let {
//                debugE("feedback: ${viewModel.recommendedWeathy.value?.feedback.isNullOrEmpty()}")
//                debugE("imgUrl: ${viewModel.recommendedWeathy.value?.imgUrl.isNullOrEmpty()}")
                if (!viewModel.recommendedWeathy.value?.feedback.isNullOrEmpty()
                        && viewModel.recommendedWeathy.value?.imgUrl.isNullOrEmpty()
                ) {
                    binding.recommended.icon2.setImageResource(R.drawable.main_ic_text)
                } else if (viewModel.recommendedWeathy.value?.feedback.isNullOrEmpty()
                        && !viewModel.recommendedWeathy.value?.imgUrl.isNullOrEmpty()
                ) {
                    binding.recommended.icon2.setImageResource(R.drawable.main_ic_image)
                } else if (!viewModel.recommendedWeathy.value?.feedback.isNullOrEmpty()
                        && !viewModel.recommendedWeathy.value?.imgUrl.isNullOrEmpty()
                ) {
                    binding.recommended.icon1.setImageResource(R.drawable.main_ic_image)
                    binding.recommended.icon2.setImageResource(R.drawable.main_ic_text)
                }
            }
        }

        binding.gpsImage setOnDebounceClickListener {
            if (locationUtil.isOtherPlaceSelected.value) {
                locationUtil.selectCurrentLocationAsPlace()
            }
        }


        var downX = 0f
        var downY = 0f
        binding.recommended.root.setOnTouchListener { v, event ->
            val eventTransfer = MotionEvent.obtain(
                    event.downTime,
                    event.eventTime,
                    event.action,
                    event.x + 26.dpFloat,
                    event.y + v.y,
                    event.metaState
            )
            binding.container.onTouchEvent(eventTransfer)
            eventTransfer.recycle()

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                }
                MotionEvent.ACTION_UP -> {
                    if (abs(downX - event.rawX) < 100 && abs(downY - event.rawY) < 100) onClickRecommendedWeathy()
                }
            }

            true
        }

        //        binding.recommended.root.setOnClickListener {
        //            onClickRecommendedWeathy()
        //        }
    }

    private fun onClickRecommendedWeathy() {
        viewModel.recommendedWeathy.value?.dailyWeather?.date?.let { date ->
            AppEvent.onNavigateCurWeathyInCalendar.tryEmit(
                    LocalDate.of(
                            date.year, date.month, date.day
                    )
            )
        }
    }

    private fun startCardAnimations() {
        binding.hourlyView.startAnimation()
        binding.weeklyView.startAnimation()
    }

    private fun resetCardAnimations() {
        binding.hourlyView.resetAnimation()
        binding.weeklyView.resetAnimation()
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
        binding.gpsImage.isEnabled = false
        binding.container.isInteractionEnabled = false
        binding.recommended.root.isEnabled = false
        (activity as MainActivity).stateButton(false)
        (activity as MainActivity).onDim()
        binding.weathyQuestion.isEnabled = false
    }

    private fun hideHelpPopup() {
        TransitionManager.beginDelayedTransition(binding.container)
        binding.weathyExplanation.alpha = 0f
        isHelpPopupShowing = false
        binding.gpsImage.isEnabled = true
        binding.container.isInteractionEnabled = true
        binding.recommended.root.isEnabled = true
        (activity as MainActivity).stateButton(true)
        (activity as MainActivity).offDim()
        binding.weathyQuestion.isEnabled = true
    }
}