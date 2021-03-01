package team.weathy.ui.license

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityLicenseDetailBinding
import team.weathy.util.setOnDebounceClickListener

class LicenseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLicenseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topText.text = intent.getStringExtra("title")
        binding.tvLicenseContent.text = intent.getStringExtra("content")

        binding.exitLicenseDetailBtn.setOnDebounceClickListener {
            finish()
        }
    }
}

