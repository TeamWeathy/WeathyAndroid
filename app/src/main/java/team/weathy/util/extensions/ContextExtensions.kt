package team.weathy.util.extensions

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()