package team.weathy.ui.inquire

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityInquireBinding
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
            finish()
        }
    }
}