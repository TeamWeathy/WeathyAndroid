package team.weathy.util

import android.view.View

val View.padding get() = ViewPadding(this)

inline class ViewPadding(private val view: View) {
    var start: Int
        get() = view.paddingStart
        set(value) = view.setPaddingRelative(value, top, end, bottom)

    var top: Int
        get() = view.paddingTop
        set(value) = view.setPaddingRelative(start, value, end, bottom)

    var end: Int
        get() = view.paddingEnd
        set(value) = view.setPaddingRelative(start, top, value, bottom)

    var bottom: Int
        get() = view.paddingBottom
        set(value) = view.setPaddingRelative(start, top, end, value)

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
}