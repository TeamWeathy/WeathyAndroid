package team.weathy.ui.nicknameset

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityNicknameSetBinding

class NicknameSetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}