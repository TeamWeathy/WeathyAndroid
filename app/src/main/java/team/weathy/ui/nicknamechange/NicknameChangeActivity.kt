package team.weathy.ui.nicknamechange

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.databinding.ActivityNicknameChangeBinding
import team.weathy.ui.setting.SettingActivity
import team.weathy.util.UniqueIdentifier
import team.weathy.util.extensions.hideKeyboard
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class NicknameChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameChangeBinding
    private val viewModel by viewModels<NicknameChangeViewModel>()

    @Inject
    lateinit var uniqueId: UniqueIdentifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameChangeBinding.inflate(layoutInflater).also {
            it.vm = viewModel
            it.lifecycleOwner = this
        }
        setContentView(binding.root)

        configureInput()
        observeViewModel()
        exitActivity()
        setNicknameHint()
    }

    private fun configureInput() {
        binding.root setOnDebounceClickListener {
            hideKeyboard()
        }
        binding.nicknameEdit.setOnFocusChangeListener { _, hasFocus ->
            viewModel.focused.value = hasFocus
        }
    }

    private fun observeViewModel() {
        viewModel.onHideKeyboard.observe(this) {
            hideKeyboard()
        }
        viewModel.onSuccess.observe(this) {
            showToast()
        }
    }

    private fun showToast() {
        showToast("닉네임이 변경되었어요!")
        startActivity(Intent(this, SettingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
        finish()
    }

    private fun exitActivity() {
        binding.exitNicknameChangeBtn.setOnDebounceClickListener {
            finish()
        }
    }

    private fun setNicknameHint() {
        binding.nicknameEdit.hint = uniqueId.userNickname.value
    }
}


