package team.weathy.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.databinding.ActivitySplashBinding
import team.weathy.ui.landing.LandingActivity
import team.weathy.ui.main.MainActivity
import team.weathy.ui.nicknameset.NicknameSetActivity
import team.weathy.util.SPUtil
import team.weathy.util.UniqueIdentifier
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var uniqueId: UniqueIdentifier

    @Inject
    lateinit var spUtil: SPUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateNextScreen()
    }

    private fun navigateNextScreen() {
        //        when {
        //            spUtil.isFirstLaunch -> navigateLanding()
        //            !uniqueId.exist -> navigateNicknameSet()
        //            else -> navigateMain()
        //        }
        navigateMain()
        //        navigateLanding()
        finish()
    }

    private fun navigateLanding() = startActivity(Intent(this, LandingActivity::class.java))

    private fun navigateNicknameSet() = startActivity(Intent(this, NicknameSetActivity::class.java))

    private fun navigateMain() = startActivity(Intent(this, MainActivity::class.java))
}