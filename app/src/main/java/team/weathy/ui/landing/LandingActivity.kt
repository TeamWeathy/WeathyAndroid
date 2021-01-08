package team.weathy.ui.landing

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import team.weathy.databinding.ActivityLandingBinding

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private val viewModel by viewModels<LandingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingBinding.inflate(layoutInflater).also {
            it.vm = viewModel
            it.lifecycleOwner = this
        }
        setContentView(binding.root)

        configurePager()
        observewPage()
    }

    private fun configurePager() = binding.pager.let {
        it.adapter = LandingAdapter()
        it.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onChangedPage(position)
            }
        })
    }

    private fun observewPage() {
        viewModel.pagerIndex.observe(this) {
            animateButton(it)
        }
    }

    private fun animateButton(idx: Int) {
        if (idx == viewModel.items.value!!.size - 1) {
            binding.start.animate().alpha(1f).setDuration(300L).withStartAction {
                binding.start.isVisible = true
            }.start()
        } else {
            binding.start.animate().alpha(0f).setDuration(300L).withEndAction {
                binding.start.isVisible = false
            }.start()
        }
    }
}