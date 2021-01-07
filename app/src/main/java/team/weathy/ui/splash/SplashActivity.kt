package team.weathy.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.weathy.MainApplication.Companion.uniqueId
import team.weathy.databinding.ActivitySplashBinding
import team.weathy.ui.main.MainActivity
import team.weathy.ui.nicknameset.NicknameSetActivity
import team.weathy.util.PermissionUtil
import team.weathy.util.extensions.showToast

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        PermissionUtil.requestLocationPermissions(this, object : PermissionUtil.PermissionListener {
            override fun onPermissionGranted() {
                navigateNextScreenAndFinish()
            }

            override fun onPermissionShouldBeGranted(deniedPermissions: List<String>) {
                showToast("권한 허용이 안되어있습니다 $deniedPermissions")
                openLocationSettings()
            }

            override fun onAnyPermissionsPermanentlyDeined(
                deniedPermissions: List<String>, permanentDeniedPermissions: List<String>
            ) {
                showToast("권한 허용이 영구적으로 거부되었습니다 $permanentDeniedPermissions")
                openLocationSettings()
            }
        })
    }

    private fun openLocationSettings() {
        PermissionUtil.openPermissionSettings(this)
    }

    private fun navigateNextScreenAndFinish() {
//        if (uniqueId.exist) {
//            navigateMain()
//        } else {
            navigateNicknameSet()
//        }
        finish()
    }

    private fun navigateNicknameSet() = startActivity(Intent(this, NicknameSetActivity::class.java))

    private fun navigateMain() = startActivity(Intent(this, MainActivity::class.java))
}