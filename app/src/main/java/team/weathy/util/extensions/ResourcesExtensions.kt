package team.weathy.util.extensions

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

fun View.getColor(@ColorRes res: Int) = context.getColor(res)
fun Fragment.getColor(@ColorRes res: Int) = requireContext().getColor(res)

fun Context.getFont(@FontRes res: Int) = ResourcesCompat.getFont(this, res)

