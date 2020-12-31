package team.weathy.ui.nicknamechange

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import team.weathy.R
import team.weathy.databinding.ActivityNicknameChangeBinding
import team.weathy.util.setOnDebounceClickListener


class NicknameChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nicknameEdit.addTextChangedListener((textWatcher))
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val length = s.toString().length
            val numOfCharactersColorMint = resources.getColor(R.color.main_mint, theme)
            val nicknameChangeBtnActive = resources.getColor(R.color.main_mint, theme)
            val numOfCharactersColorGrey = resources.getColor(R.color.sub_grey_6, theme)
            val nicknameChangeBtnInactive = resources.getColor(R.color.sub_grey_3, theme)

            binding.numOfCharacters1.text = "$length"
            binding.numOfCharacters2.text = "/6"

            if (length > 0) {
                binding.changeNicknameBtn.isEnabled = true
                binding.numOfCharacters1.setTextColor(numOfCharactersColorMint)
                binding.changeNicknameBtn.setBackgroundColor(nicknameChangeBtnActive)
                binding.nicknameEdit.setBackgroundResource(R.drawable.nickname_edit_border_active)
                changeNicknameBtnClick()
            } else {
                binding.changeNicknameBtn.isEnabled = false
                binding.numOfCharacters1.setTextColor(numOfCharactersColorGrey)
                binding.changeNicknameBtn.setBackgroundColor(nicknameChangeBtnInactive)
                binding.nicknameEdit.setBackgroundResource(R.drawable.nickname_edit_border)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    }
    
    private fun changeNicknameBtnClick() {
        binding.changeNicknameBtn.setOnDebounceClickListener {
            Toast.makeText(this, "변경되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}