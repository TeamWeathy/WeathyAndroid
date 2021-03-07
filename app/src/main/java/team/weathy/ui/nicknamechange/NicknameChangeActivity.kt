package team.weathy.ui.nicknamechange

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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

        showKeyboard()
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
            setCountVisibility(hasFocus)
        }
        binding.nicknameEdit.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                return@OnKeyListener true
            }
            false
        })
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

    private fun setCountVisibility(hasFocus: Boolean) {
        binding.count.isVisible = hasFocus
        binding.maxLength.isVisible = hasFocus
    }

    private fun showKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        setKeyboardMode()
    }

    private fun setKeyboardMode() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}


