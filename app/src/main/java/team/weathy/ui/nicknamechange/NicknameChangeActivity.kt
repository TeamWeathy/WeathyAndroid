package team.weathy.ui.nicknamechange

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import team.weathy.R
import team.weathy.databinding.ActivityNicknameChangeBinding
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener


class NicknameChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nicknameEdit.addTextChangedListener(textWatcher)
        exitNicknameChange()
        checkBlank()
        checkBackground()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val length = s.toString().length
            binding.numOfCharacters1.text = "$length"
            binding.numOfCharacters2.text = "/6"

            if (length > 0) {
                binding.changeNicknameBtn.isEnabled = true
                binding.deleteNicknameBtn.isEnabled = true
                binding.numOfCharacters1.setTextColor(getColor(R.color.main_mint))
                binding.nicknameEdit.setBackgroundResource(R.drawable.edit_border_active)
                binding.deleteNicknameBtn.visibility = View.VISIBLE
                deleteNickname()
                changeNicknameBtnClick()
            } else {
                binding.changeNicknameBtn.isEnabled = false
                binding.deleteNicknameBtn.isEnabled = false
                binding.numOfCharacters1.setTextColor(getColor(R.color.sub_grey_6))
                binding.nicknameEdit.setBackgroundResource(R.drawable.edit_border)
                binding.deleteNicknameBtn.visibility = View.INVISIBLE
            }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun changeNicknameBtnClick() {
        binding.changeNicknameBtn.setOnDebounceClickListener {
            showToast("닉네임이 변경되었습니다.")
            finish()
        }
    }

    private fun deleteNickname() {
        binding.deleteNicknameBtn.setOnDebounceClickListener {
            binding.nicknameEdit.setText("")
        }
    }

    private fun exitNicknameChange() {
        binding.exitNicknameChangeBtn.setOnDebounceClickListener {
            finish()
        }
    }

    private fun setCountVisibility(hasFocus: Boolean) {
        binding.numOfCharacters1.isVisible = hasFocus
        binding.numOfCharacters2.isVisible = hasFocus
    }

    private fun checkBlank() {
        binding.nicknameEdit.setOnFocusChangeListener { _, hasFocus ->
            setCountVisibility(hasFocus)

            var getEdit = binding.nicknameEdit.text.toString()
            if (getEdit.equals("")) {
                binding.nicknameEdit.setBackgroundResource(R.drawable.edit_border)
                binding.deleteNicknameBtn.visibility = View.INVISIBLE
            } else {
                binding.nicknameEdit.setBackgroundResource(R.drawable.edit_border_active)
                binding.deleteNicknameBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun checkBackground() {
        binding.layoutNickname.setOnDebounceClickListener {
            binding.nicknameEdit.clearFocus()
            binding.nicknameEdit.setBackgroundResource(R.drawable.edit_border)
            binding.deleteNicknameBtn.visibility = View.INVISIBLE
        }
    }
}