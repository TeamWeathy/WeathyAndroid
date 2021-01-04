package team.weathy.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

class Once<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

fun <T> LiveData<Once<T>>.observeOnce(lifecycle: LifecycleOwner, listener: (T) -> Unit) {
    this.observe(lifecycle, {
        it?.getContentIfNotHandled()?.let { content ->
            listener(content)
        }
    })
}