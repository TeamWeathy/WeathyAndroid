package team.weathy.util.extensions

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import team.weathy.R
import team.weathy.databinding.ToastCommonBinding
import kotlin.math.roundToInt

fun Activity.hideKeyboard() {
    val imm = getSystemService(InputMethodManager::class.java)

    imm.hideSoftInputFromWindow(currentFocus?.rootView?.windowToken, 0)
    currentFocus?.rootView?.findFocus()?.clearFocus()
}

fun Context.showToast(message: String) {
    val binding = ToastCommonBinding.inflate(LayoutInflater.from(this))
    binding.text.text = message

    Toast(this).apply {
        setGravity(Gravity.BOTTOM, 0, (resources.displayMetrics.density * 96).roundToInt())


        view = binding.root
    }.show()

}