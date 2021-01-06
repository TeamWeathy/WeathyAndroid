package team.weathy.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import team.weathy.BuildConfig

val View.margin get() = ViewMargin(this)

inline class ViewMargin(private val view: View) {
    var start: Int
        get() = view.marginStart
        set(value) = applyMargin { it.marginStart = value }

    var top: Int
        get() = view.marginTop
        set(value) = applyMargin { it.topMargin = value }

    var end: Int
        get() = view.marginEnd
        set(value) = applyMargin { it.marginEnd = value }

    var bottom: Int
        get() = view.marginBottom
        set(value) = applyMargin { it.bottomMargin = value }

    var horizontal: Int
        get() = start + end
        set(value) {
            start = value
            end = value
        }

    var vertical: Int
        get() = top + bottom
        set(value) {
            top = value
            bottom = value
        }

    var total: Int
        @Deprecated(
            "No getter for that field", level = DeprecationLevel.HIDDEN
        ) get() = throw UnsupportedOperationException("No getter for such field")
        set(value) {
            horizontal = value
            vertical = value
        }

    private inline fun applyMargin(body: (params: ViewGroup.MarginLayoutParams) -> Unit) {
        val params = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (params != null) {
            body(params)
        } else {
            if (BuildConfig.DEBUG) {
                throw IllegalStateException("Parent layout doesn't support margins")
            }
        }
    }
}