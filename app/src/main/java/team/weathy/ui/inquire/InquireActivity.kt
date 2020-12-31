package team.weathy.ui.inquire

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityInquireBinding
import team.weathy.databinding.ActivityLandingBinding
import team.weathy.ui.setting.SettingActivity
import team.weathy.util.setOnDebounceClickListener

class InquireActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInquireBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInquireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        exitInquire()
    }

    private fun exitInquire() {
        binding.exitInquireBtn.setOnDebounceClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}