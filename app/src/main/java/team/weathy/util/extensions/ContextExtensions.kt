package team.weathy.util.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

fun Context.showToast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
fun Activity.hideKeyboard() {
    val imm = getSystemService(InputMethodManager::class.java)

    imm.hideSoftInputFromWindow(currentFocus?.rootView?.windowToken, 0)
    currentFocus?.rootView?.findFocus()?.clearFocus()
}