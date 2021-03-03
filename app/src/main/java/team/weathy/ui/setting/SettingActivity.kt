package team.weathy.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import team.weathy.R
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

        configureNavigation()
    }

    private fun configureNavigation() {

        binding.nicknameChangeClick.setOnDebounceClickListener {
            val intent = Intent(this, NicknameChangeActivity::class.java)
            startActivity(intent)

        }
        binding.developerClick.setOnDebounceClickListener {
            val intent = Intent(this, DeveloperInfoActivity::class.java)
            startActivity(intent)
        }

        binding.exitSettingBtn.setOnDebounceClickListener {
            finish()
        }

        binding.inquireClick.setOnDebounceClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_receiver)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_title))
            intent.type = "message/rfc822"
            startActivity(intent)
        }

        binding.infoClick.setOnDebounceClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.notion.so/5a2f164b5afe46559c78d7efed6d3e8a")
            )
            startActivity(intent)
        }

        binding.locationClick.setOnDebounceClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.notion.so/69010f5bca1e443ca3b47cfd74bdb41f")
            )
            startActivity(intent)
        }

        binding.licenseClick.setOnDebounceClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            OssLicensesMenuActivity.setActivityTitle("Ossl Title")
        }
    }
}
