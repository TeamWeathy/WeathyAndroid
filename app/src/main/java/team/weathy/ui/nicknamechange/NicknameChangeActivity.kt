package team.weathy.ui.nicknamechange

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.databinding.ActivityNicknameChangeBinding
import team.weathy.ui.main.MainActivity
import team.weathy.util.extensions.hideKeyboard
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener

@FlowPreview
@AndroidEntryPoint
class NicknameChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameChangeBinding
    private val viewModel by viewModels<NicknameChangeViewModel>()

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
            navigateMain()
        }
    }

    private fun navigateMain() {
        showToast("닉네임이 변경되었어요!")
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
        finish()
    }

    private fun exitActivity() {
        binding.exitNicknameChangeBtn.setOnDebounceClickListener {
            finish()
        }
    }
}


