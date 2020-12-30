package team.weathy.ui.nicknamechange

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import team.weathy.R
import team.weathy.databinding.ActivityNicknameChangeBinding


class NicknameChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameChangeBinding
    private val textWatcher = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            val length = s.toString().length
            binding.numOfCharacters.text = "$length / 6"

            if(length > 0){
                val nickname_change_btn_active  = resources.getColor(R.color.main_mint)
                binding.changeNicknameBtn.setBackgroundColor(nickname_change_btn_active)
            }
            else{
                val nickname_change_btn_inactive  = resources.getColor(R.color.sub_grey_3)
                binding.changeNicknameBtn.setBackgroundColor(nickname_change_btn_inactive)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNicknameChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nicknameEdit.addTextChangedListener((textWatcher))
    }


}