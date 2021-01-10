package team.weathy.util.extensions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <R> ViewModel.launchCatch(
    block: suspend CoroutineScope.() -> R,
    loading: MutableLiveData<Boolean>? = null,
    onSuccess: ((result: R) -> Unit)? = null,
    onFailure: ((e: Throwable) -> Unit)? = null,
    onFinally: (() -> Unit)? = null,
) {
    viewModelScope.launch {
        loading?.value = true
        runCatching<R> {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            onSuccess?.invoke(it)
        }.onFailure {
            onFailure?.invoke(it)
        }
        onFinally?.invoke()
        loading?.value = false
    }
}

