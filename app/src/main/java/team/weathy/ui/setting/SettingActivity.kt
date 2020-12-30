package team.weathy.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivitySettingBinding
import team.weathy.ui.developerinfo.DeveloperInfoActivity
import team.weathy.ui.inquire.InquireActivity
import team.weathy.ui.nicknamechange.NicknameChangeActivity
import team.weathy.util.setOnDebounceClickListener

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        go_nicknameChange()
        go_inquire()
        go_developerInfo()

    }

    fun go_nicknameChange(){
        binding.changeNicknameBtn.setOnDebounceClickListener {
            val intent = Intent(this, NicknameChangeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun go_inquire(){
        binding.inquireBtn.setOnDebounceClickListener {
            val intent = Intent(this, InquireActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun go_developerInfo(){
        binding.developerInfoBtn.setOnDebounceClickListener {
            val intent = Intent(this, DeveloperInfoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}