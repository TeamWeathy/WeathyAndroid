package team.weathy.ui.nicknameset

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import team.weathy.R
import team.weathy.databinding.ActivityNicknameSetBinding
import team.weathy.dialog.AccessDialog
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
class NicknameSetActivity : AppCompatActivity(), AccessDialog.ClickListener {
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

        showKeyboard()
        configureTitle()
        configureInput()
        observeViewModel()
    }

    private fun configureInput() {
        binding.root setOnDebounceClickListener {
            hideKeyboard()
        }
        binding.input.setOnFocusChangeListener { _, hasFocus ->
            setCountVisibility(hasFocus)
        }
        binding.input.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                return@OnKeyListener true
            }
            false
        })
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
            showAccessDialog()
        }
    }

    override fun navigateMainWithPermissionCheck() {
        PermissionUtil.requestLocationPermissions(this, object : PermissionListener {
            override fun onPermissionGranted() {
                locationUtil.registerLocationListener()

                lifecycleScope.launchWhenStarted {
                    viewModel.loadingSubmit.value = true
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

    private fun showAccessDialog() {
        AccessDialog.newInstance(
                "웨디 앱 접근 권한 안내",
                "위치정보",
                "사진/카메라",
                "현재 위치를 중심으로 날씨 정보를 알려드려요.",
                "날씨 기록에 사진을 등록할 수 있어요.",
                "선택 항목은 허용하지 않더라도 앱 이용이 가능해요.",
                "확인",
                getColor(R.color.mint_main)).show(supportFragmentManager, null)
    }

    private fun setCountVisibility(hasFocus: Boolean) {
        binding.count.isVisible = hasFocus
        binding.maxLength.isVisible = hasFocus
    }

    private fun showKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        setKeyboardMode()
    }

    private fun setKeyboardMode() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}