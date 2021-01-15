package team.weathy.ui.nicknameset

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import team.weathy.R
import team.weathy.databinding.ActivityNicknameSetBinding
import team.weathy.ui.main.MainActivity
import team.weathy.util.PermissionUtil
import team.weathy.util.PermissionUtil.PermissionListener
import team.weathy.util.extensions.font
import team.weathy.util.extensions.getFont
import team.weathy.util.extensions.hideKeyboard
import team.weathy.util.location.LocationUtil
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class NicknameSetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameSetBinding
    private val viewModel by viewModels<NicknameSetViewModel>()

    @Inject
    lateinit var locationUtil: LocationUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameSetBinding.inflate(layoutInflater).also {
            it.vm = viewModel
            it.lifecycleOwner = this
        }
        setContentView(binding.root)

        configureTitle()
        observeViewModel()
        configureInput()
    }

    private fun configureInput() {
        binding.root setOnDebounceClickListener {
            hideKeyboard()
        }
        binding.input.setOnFocusChangeListener { _, hasFocus ->
            viewModel.focused.value = hasFocus
        }
    }

    private fun configureTitle() {
        binding.title.text = buildSpannedString {
            appendLine("웨디에서 사용할")
            color(getColor(R.color.main_mint)) {
                font(getFont(R.font.notosans_medium)) {
                    append("닉네임")
                }
            }
            append("을 입력해주세요.")
        }
    }

    private fun observeViewModel() {
        viewModel.onHideKeyboard.observe(this) {
            hideKeyboard()
        }
        viewModel.onSuccess.observe(this) {
            navigateMainWithPermissionCheck()
        }
    }

    private fun navigateMainWithPermissionCheck() {
        PermissionUtil.requestLocationPermissions(this, object : PermissionListener {
            override fun onPermissionGranted() {
                locationUtil.registerLocationListener()

                lifecycleScope.launchWhenStarted {
                    delay(3000)
                    navigateMain()
                }
            }

            override fun onPermissionShouldBeGranted(deniedPermissions: List<String>) {
                // TODO
            }

            override fun onAnyPermissionsPermanentlyDeined(
                deniedPermissions: List<String>, permanentDeniedPermissions: List<String>
            ) {
                // TODO
            }
        })
    }

    private fun navigateMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}