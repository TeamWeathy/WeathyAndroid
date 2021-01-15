package team.weathy.ui.landing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import team.weathy.R
import team.weathy.databinding.ActivityLandingBinding
import team.weathy.ui.main.MainActivity
import team.weathy.ui.nicknameset.NicknameSetActivity
import team.weathy.util.PermissionUtil
import team.weathy.util.PermissionUtil.PermissionListener
import team.weathy.util.UniqueIdentifier
import team.weathy.util.__TEST__IDLING__
import team.weathy.util.extensions.px
import team.weathy.util.location.LocationUtil
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private val viewModel by viewModels<LandingViewModel>()

    @Inject
    lateinit var uniqueId: UniqueIdentifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preloadDrawables()

        binding = ActivityLandingBinding.inflate(layoutInflater).also {
            it.vm = viewModel
            it.lifecycleOwner = this
        }
        setContentView(binding.root)

        configurePager()
        configureLottieAnimations()

        reserveMockUpImageLayoutCalculation()

        binding.start setOnDebounceClickListener {
            navigateNextScreen()
        }

        // fIXME
        PermissionUtil.requestLocationPermissions(this, object : PermissionListener {
            override fun onPermissionGranted() {
                locationUtil.registerLocationListener()
                lifecycleScope.launchWhenStarted {
                    delay(3500L)

                }

            }
        })
    }

    // FIXME 시연용
    @Inject
    lateinit var locationUtil: LocationUtil

    private fun preloadDrawables() {
        listOf(
            R.drawable.onboarding_img_phone_1,
            R.drawable.onboarding_img_phone_2,
            R.drawable.onboarding_img_phone_3,
            R.drawable.onboarding_img_popup_1,
            R.drawable.onboarding_img_popup_2,
            R.drawable.onboarding_img_popup_3
        ).forEach {
            getDrawable(it)
        }
    }

    private fun navigateNextScreen() {
        //        when {
        //            !uniqueId.exist -> navigateNicknameSet()
        //            else -> navigateMain()
        //        }

        navigateMain()
        finish()
    }

    private fun navigateNicknameSet() = startActivity(Intent(this, NicknameSetActivity::class.java))

    private fun navigateMain() = startActivity(Intent(this, MainActivity::class.java))

    private fun reserveMockUpImageLayoutCalculation() = binding.mockup.doOnLayout {
        mokcUpImageHeight = it.height
        mockUpImageWidth = it.width
    }

    private fun configurePager() = binding.pager.let {
        it.offscreenPageLimit = 1
        it.adapter = LandingAdapter()
        it.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onChangedPage(position)
            }
        })
    }

    private fun configureLottieAnimations() {
        val lotties = listOf(binding.lottie1, binding.lottie2, binding.lottie3)

        viewModel.pagerIndex.observe(this) { index ->
            val previousIndex = viewModel.previousPageIndex

            lotties.forEach {
                it.pauseAnimation()
            }
            if (previousIndex > index) {
                lotties.forEachIndexed { idx, view -> view.isInvisible = previousIndex != idx }
                lotties[previousIndex].speed = -1f

                if (lotties[index].isAnimating) lotties[previousIndex].progress = 1f
                lotties[previousIndex].resumeAnimation()

            } else {
                lotties.forEachIndexed { idx, view -> view.isInvisible = index != idx }
                lotties[index].speed = 1f

                if (lotties[previousIndex].isAnimating) lotties[index].progress = 0f
                lotties[index].resumeAnimation()
            }
        }
    }

    companion object {
        const val ITEM_COUNT = 3
        private var mokcUpImageHeight = 0
        private var mockUpImageWidth = 0
        private val POPUP_TOP_PERCENTS = listOf(0.55f, 0.19f, 0.22f)

        @BindingAdapter("landing_button")
        @JvmStatic
        fun Button.setLandingbuttonAnimation(idx: Int) {
            __TEST__IDLING__.increment()
            if (idx == ITEM_COUNT - 1) {
                animate().alpha(1f).setDuration(500L).setStartDelay(800L).withStartAction {
                    isVisible = true
                }.withEndAction { __TEST__IDLING__.decrement() }.start()
            } else {
                animate().alpha(0f).setDuration(500L).setStartDelay(0L).withEndAction {
                    isVisible = false
                }.withEndAction { __TEST__IDLING__.decrement() }.start()
            }
        }

        @BindingAdapter("landing_indicator")
        @JvmStatic
        fun View.setLandingIndicatorAnimation(selected: Boolean) {
            if (selected) {
                animate().alpha(1f).scaleX(1.5f).scaleY(1.5f).setDuration(500L).start()
            } else {
                animate().alpha(0.4f).scaleX(1f).scaleY(1f).setDuration(500L).start()
            }
        }

        @BindingAdapter("landing_mockup")
        @JvmStatic
        fun ImageView.setLandingMockUpImage(idx: Int) {
            setImageResource(
                when (idx) {
                    0 -> R.drawable.onboarding_img_phone_1
                    1 -> R.drawable.onboarding_img_phone_2
                    2 -> R.drawable.onboarding_img_phone_3
                    else -> throw IllegalArgumentException()
                }
            )
        }

        @BindingAdapter("landing_popup")
        @JvmStatic
        fun ImageView.setLandingPopUpImage(idx: Int) {
            doOnLayout {
                alpha = 0f

                updateLayoutParams {
                    width = mockUpImageWidth * when (idx) {
                        0 -> 318
                        1 -> 315
                        else -> 326
                    } / 346
                }

                scaleX = 0.75f
                scaleY = 0.75f
                setImageResource(
                    when (idx) {
                        0 -> R.drawable.onboarding_img_popup_1
                        1 -> R.drawable.onboarding_img_popup_2
                        2 -> R.drawable.onboarding_img_popup_3
                        else -> throw IllegalArgumentException()
                    }
                )

                val dest = mokcUpImageHeight * POPUP_TOP_PERCENTS[idx]
                translationY = (dest + when (idx) {
                    1 -> px(-25)
                    else -> px(25)
                })
                animate().alpha(1f).translationY(dest).scaleX(1f).scaleY(1f).setDuration(800L).start()
            }
        }
    }
}