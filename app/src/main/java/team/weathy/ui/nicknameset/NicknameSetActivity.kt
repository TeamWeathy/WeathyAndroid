package team.weathy.ui.nicknameset

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.R
import team.weathy.databinding.ActivityNicknameSetBinding
import team.weathy.ui.main.MainActivity
import team.weathy.util.extensions.font
import team.weathy.util.extensions.getFont
import team.weathy.util.extensions.hideKeyboard

@AndroidEntryPoint
class NicknameSetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameSetBinding
    private val viewModel by viewModels<NicknameSetViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameSetBinding.inflate(layoutInflater).also {
            it.vm = viewModel
            it.lifecycleOwner = this
        }
        setContentView(binding.root)

        configureTitle()
        observeViewModel()
    }

    private fun configureTitle() {
        binding.title.text = buildSpannedString {
            appendLine("웨디에서 사용할")
            color(getColor(R.color.main_mint)) {
                font(getFont(R.font.notosans_medium)) {
                    append("닉네임")
                }
            }
            append("을 입력해주세요.")
        }
    }

    private fun observeViewModel() {
        viewModel.onHideKeyboard.observe(this) {
            hideKeyboard()
        }
        viewModel.onSuccess.observe(this) {
            navigateMain()
        }
    }

    private fun navigateMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}