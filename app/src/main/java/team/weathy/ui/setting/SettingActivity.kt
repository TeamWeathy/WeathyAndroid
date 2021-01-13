package team.weathy.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivitySettingBinding
import team.weathy.ui.developerinfo.DeveloperInfoActivity
import team.weathy.ui.inquire.InquireActivity
import team.weathy.ui.nicknamechange.NicknameChangeActivity
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goNicknameChange()
        goInquire()
        goDeveloperInfo()
        pushNotifSwitchAct()
        exitSetting()
    }

    private fun goNicknameChange() {
        binding.changeNicknameClick.setOnDebounceClickListener {
            val intent = Intent(this, NicknameChangeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goInquire() {
        binding.inquireClick.setOnDebounceClickListener {
            val intent = Intent(this, InquireActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goDeveloperInfo() {
        binding.developerInfoClick.setOnDebounceClickListener {
            val intent = Intent(this, DeveloperInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun pushNotifSwitchAct() {
        binding.pushNotifSwitch.setOnCheckedChangeListener { _, onSwitch ->
            if (onSwitch) {
                showToast(" 현재 위치에 따라 날씨를 알려드릴게요!")
            }
        }
    }

    private fun exitSetting() {
        binding.exitSettingBtn.setOnDebounceClickListener {
            finish()
        }
    }
}
