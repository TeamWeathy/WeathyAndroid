package team.weathy.ui.record.detail

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.Fragment
import team.weathy.R
import team.weathy.databinding.FragmentRecordDetailBinding
import team.weathy.ui.main.MainActivity
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener

class RecordDetailFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordDetailBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordDetailBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.etDetail.addTextChangedListener((textWatcher))

        binding.layoutDetail setOnDebounceClickListener {
            hideKeyboard()
            binding.etDetail.clearFocus()
        }

        setDialogClickListener()
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

    private fun setDialogClickListener() {
        binding.btnConfirm setOnDebounceClickListener  {
            val dialog = AlertDialog.Builder(context).create()
            val edialog: LayoutInflater = LayoutInflater.from(context)
            val mView: View = edialog.inflate(R.layout.dialog_record_complete, null)

            val confirm: Button = mView.findViewById(R.id.btn_confirm)

            confirm setOnDebounceClickListener {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)

                dialog.dismiss()
                dialog.cancel()
            }

            // Dialog Background 설정
            val color = ColorDrawable(Color.TRANSPARENT)
            // Dialog 크기 설정
            val inset = InsetDrawable(color, 66)
            dialog.window?.setBackgroundDrawable(inset)

            dialog.setView(mView)
            dialog.create()
            dialog.show()
        }
    }
}