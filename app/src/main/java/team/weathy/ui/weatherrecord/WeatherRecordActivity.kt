package team.weathy.ui.weatherrecord

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityWeatherRecordBinding

class WeatherRecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}