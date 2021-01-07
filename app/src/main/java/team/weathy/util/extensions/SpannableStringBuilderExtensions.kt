package team.weathy.util.extensions

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.core.text.inSpans

inline fun SpannableStringBuilder.font(typeface: Typeface? = null, builderAction: SpannableStringBuilder.() -> Unit) =
    inSpans(StyleSpan(typeface?.style ?: Typeface.DEFAULT.style), builderAction = builderAction)