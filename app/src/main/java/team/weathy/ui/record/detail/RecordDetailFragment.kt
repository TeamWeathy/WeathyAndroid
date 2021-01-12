package team.weathy.ui.record.detail

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import team.weathy.R
import team.weathy.databinding.FragmentRecordDetailBinding
import team.weathy.ui.record.RecordActivity
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.showToast
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

        binding.etDetail.setOnFocusChangeListener { _, hasFocus ->
            setCountVisibility(hasFocus)
        }

        configureStartNavigation()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(string: Editable?) {
            val length = string.toString().length
            binding.tvTextLength2.text = "$length"

            if (length > 0) {
                setButtonActivation(true, getColor(R.color.main_mint))
                setTextActivation(getColor(R.color.main_mint), R.drawable.edit_border_active)
            } else {
                setButtonActivation(false, getColor(R.color.sub_grey_3))
                setTextActivation(getColor(R.color.sub_grey_6), R.drawable.edit_border)
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

    private fun configureStartNavigation() {
        binding.close setOnDebounceClickListener {
            requireActivity().finish()
        }
        binding.btnConfirm setOnDebounceClickListener {
            requireContext().showToast("웨디에 내용이 추가되었어요!")
        }
    }

    private fun setCountVisibility(hasFocus: Boolean) {
        binding.tvTextLength.isVisible = hasFocus
        binding.tvTextLength2.isVisible = hasFocus
    }

    private fun setButtonActivation(isEnable: Boolean, color: Int) {
        binding.btnConfirm.isEnabled = isEnable
        binding.btnConfirm.setBackgroundColor(color)
    }

    private fun setTextActivation(color: Int, drawable: Int) {
        binding.tvTextLength2.setTextColor(color)
        binding.etDetail.setBackgroundResource(drawable)
    }
}