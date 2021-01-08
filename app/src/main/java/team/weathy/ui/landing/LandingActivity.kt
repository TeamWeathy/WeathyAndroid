package team.weathy.ui.landing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.R
import team.weathy.databinding.ActivityLandingBinding
import team.weathy.ui.main.MainActivity
import team.weathy.ui.nicknameset.NicknameSetActivity
import team.weathy.util.UniqueIdentifier
import team.weathy.util.extensions.px
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private val viewModel by viewModels<LandingViewModel>()

    @Inject
    lateinit var uniqueId: UniqueIdentifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingBinding.inflate(layoutInflater).also {
            it.vm = viewModel
            it.lifecycleOwner = this
        }
        setContentView(binding.root)

        configurePager()

        reserveMockUpImageLayoutCalculation()

        binding.start setOnDebounceClickListener {
            navigateNextScreen()
        }
    }

    private fun navigateNextScreen() {
        when {
            !uniqueId.exist -> navigateNicknameSet()
            else -> navigateMain()
        }
        finish()
    }

    private fun navigateNicknameSet() = startActivity(Intent(this, NicknameSetActivity::class.java))

    private fun navigateMain() = startActivity(Intent(this, MainActivity::class.java))

    private fun reserveMockUpImageLayoutCalculation() = binding.mockup.doOnLayout {
        mokcUpImageHeight = it.height
        mockUpImageWidth = it.width
    }

    private fun configurePager() = binding.pager.let {
        it.adapter = LandingAdapter()
        it.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onChangedPage(position)
            }
        })
    }

    companion object {
        const val ITEM_COUNT = 3
        private var mokcUpImageHeight = 0
        private var mockUpImageWidth = 0
        private val POPUP_TOP_PERCENTS = listOf(0.52f, 0.156f, 0.19f)

        @BindingAdapter("landing_button")
        @JvmStatic
        fun Button.setLandingbuttonAnimation(idx: Int) {
            if (idx == ITEM_COUNT - 1) {
                animate().alpha(1f).setDuration(500L).setStartDelay(800L).withStartAction {
                    isVisible = true
                }.start()
            } else {
                animate().alpha(0f).setDuration(500L).setStartDelay(0L).withEndAction {
                    isVisible = false
                }.start()
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
                    } / 360
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