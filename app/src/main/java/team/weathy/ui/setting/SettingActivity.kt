package team.weathy.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivitySettingBinding
import team.weathy.ui.developerinfo.DeveloperInfoActivity
import team.weathy.ui.nicknamechange.NicknameChangeActivity
import team.weathy.util.setOnDebounceClickListener


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goNicknameChange()
        goDeveloperInfo()
        exitSetting()
    }

    private fun goNicknameChange() {
        binding.nicknameChangeClick.setOnDebounceClickListener {
            val intent = Intent(this, NicknameChangeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goDeveloperInfo() {
        binding.developerClick.setOnDebounceClickListener {
            val intent = Intent(this, DeveloperInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun exitSetting() {
        binding.exitSettingBtn.setOnDebounceClickListener {
            finish()
        }
    }
}
