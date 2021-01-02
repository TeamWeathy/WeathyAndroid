package team.weathy.ui.developerinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityDeveloperInfoBinding
import team.weathy.util.setOnDebounceClickListener

class DeveloperInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeveloperInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeveloperInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        exitDeveloperInfo()
    }

    private fun exitDeveloperInfo() {
        binding.exitDeveloperInfoBtn.setOnDebounceClickListener {
            finish()
        }
    }
}