package team.weathy.ui.nicknamechange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityNicknameChangeBinding

class NicknameChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}