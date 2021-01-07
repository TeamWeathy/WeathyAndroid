package team.weathy.ui.nicknameset

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.databinding.ActivityNicknameSetBinding

@AndroidEntryPoint
class NicknameSetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameSetBinding
    private val viewModel by viewModels<NicknameSetViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}