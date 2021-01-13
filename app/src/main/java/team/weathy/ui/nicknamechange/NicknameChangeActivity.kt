package team.weathy.ui.nicknamechange

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.databinding.ActivityNicknameChangeBinding
import team.weathy.ui.main.MainActivity
import team.weathy.util.extensions.hideKeyboard
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

        observeViewModel()
        deleteNickname()
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

    private fun deleteNickname() {
        binding.deleteNicknameBtn.setOnDebounceClickListener {
            binding.nicnknameEdit.setText("")
        }
    }
}

