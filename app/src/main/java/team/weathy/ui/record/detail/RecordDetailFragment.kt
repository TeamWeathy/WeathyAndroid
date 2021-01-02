package team.weathy.ui.record.detail

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import team.weathy.R
import team.weathy.databinding.FragmentRecordDetailBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener

class RecordDetailFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordDetailBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordDetailBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.etDetail.addTextChangedListener(textWatcher)

        binding.layoutDetail setOnDebounceClickListener {
            hideKeyboard()
            binding.etDetail.clearFocus()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(string: Editable?) {
            val length = string.toString().length
            binding.tvTextLength2.text = "$length"

            if (length > 0) {
                binding.tvTextLength2.setTextColor(resources.getColor(R.color.main_mint))
            } else {
                binding.tvTextLength2.setTextColor(resources.getColor(R.color.sub_grey_6))
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.etDetail.windowToken, 0)
    }
}