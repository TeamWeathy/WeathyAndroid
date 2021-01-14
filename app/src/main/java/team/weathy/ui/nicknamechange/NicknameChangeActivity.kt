package team.weathy.ui.nicknamechange

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.R
import team.weathy.databinding.ActivityNicknameChangeBinding
import team.weathy.ui.main.MainActivity
import team.weathy.util.extensions.hideKeyboard
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener

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

        binding.nicknameEdit.addTextChangedListener(textWatcher)
        observeViewModel()
        deleteNickname()
        exitActivity()
        checkBackground()
        checkBlank()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val length = s.toString().length

            if (length > 0) {
                binding.deleteNicknameBtn.visibility = View.VISIBLE
                if (!binding.submit.isEnabled)
                    setButtonEnabled(true)
            } else {
                binding.deleteNicknameBtn.visibility = View.INVISIBLE
                if (binding.submit.isEnabled)
                    setButtonDisabled(false)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun deleteNickname() {
        binding.deleteNicknameBtn.setOnDebounceClickListener {
            binding.nicknameEdit.setText("")
        }
    }

    private fun exitActivity() {
        binding.exitNicknameChangeBtn.setOnDebounceClickListener {
            finish()
        }
    }

    private fun checkBackground() {
        binding.layoutNickname.setOnDebounceClickListener {
            binding.nicknameEdit.clearFocus()
        }
    }

    private fun setCountVisibility(hasFocus: Boolean) {
        var getEdit = binding.nicknameEdit.text.toString()

        binding.maxLength.isVisible = hasFocus
        binding.countLength.isVisible = hasFocus

        if (getEdit.equals("")) {
            binding.deleteNicknameBtn.visibility = View.INVISIBLE
        } else {
            binding.deleteNicknameBtn.isVisible = hasFocus
        }
    }

    private fun checkBlank() {
        binding.nicknameEdit.setOnFocusChangeListener { _, hasFocus ->
            setCountVisibility(hasFocus)
        }
    }


    private fun setButtonEnabled(isEnable: Boolean) {
        val colorChangeActive = AnimatorInflater.loadAnimator(this, R.animator.color_change_active_anim) as AnimatorSet
        colorChangeActive.setTarget(binding.submit)
        colorChangeActive.start()
        binding.submit.isEnabled = isEnable
    }

    private fun setButtonDisabled(isEnable: Boolean) {
        val colorChange = AnimatorInflater.loadAnimator(this, R.animator.color_change_anim) as AnimatorSet
        colorChange.setTarget(binding.submit)
        colorChange.start()
        binding.submit.isEnabled = isEnable
    }
}


